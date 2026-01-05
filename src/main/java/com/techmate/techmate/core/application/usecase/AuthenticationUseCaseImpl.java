package com.techmate.techmate.core.application.usecase.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techmate.techmate.core.domain.model.user.User;
import com.techmate.techmate.core.application.port.input.AuthenticationUseCase;
import com.techmate.techmate.core.application.port.output.UserRepositoryPort;
import com.techmate.techmate.core.application.port.output.PasswordEncoderPort;
import com.techmate.techmate.core.application.port.output.TokenGeneratorPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of authentication use cases.
 * 
 * Handles user login and token generation/validation.
 * Coordinates with user repository, password encoder, and token generator ports.
 */
@Service("authenticationUseCaseHex")
@Transactional
public class AuthenticationUseCaseImpl implements AuthenticationUseCase {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationUseCaseImpl.class);
    private static final long TOKEN_EXPIRATION_SECONDS = 3600;  // 1 hour

    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final TokenGeneratorPort tokenGenerator;

    public AuthenticationUseCaseImpl(
            UserRepositoryPort userRepository,
            PasswordEncoderPort passwordEncoder,
            TokenGeneratorPort tokenGenerator) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenGenerator = tokenGenerator;
    }

    @Override
    public AuthenticationResponse authenticate(String email, String plainPassword) {
        logger.info("Authenticating user: {}", email);

        // Find user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("User not found: {}", email);
                    return new RuntimeException("Invalid credentials");
                });

        // Check if user is enabled
        if (!user.isEnabled()) {
            logger.warn("Attempt to authenticate disabled user: {}", email);
            throw new RuntimeException("User account is disabled");
        }

        // Verify password
        if (!passwordEncoder.matches(plainPassword, user.getPasswordHash())) {
            logger.warn("Invalid password for user: {}", email);
            throw new RuntimeException("Invalid credentials");
        }

        // Generate token
        List<String> roles = new ArrayList<>(user.getRoleNames());
        String token = tokenGenerator.generateToken(user.getId(), user.getUsername(), user.getEmail(), roles);

        // Update last login time
        userRepository.updateLastLoginTime(user.getId());

        logger.info("User authenticated successfully: {}", email);
        return new AuthenticationResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                token,
                roles,
                TOKEN_EXPIRATION_SECONDS
        );
    }

    @Override
    public boolean validateToken(String token) {
        logger.debug("Validating token");
        return tokenGenerator.isValid(token);
    }

    @Override
    public String generateToken(Integer userId, String username, String email, List<String> roles) {
        logger.debug("Generating token for user: {}", username);
        return tokenGenerator.generateToken(userId, username, email, roles);
    }

    @Override
    public boolean isTokenValid(String token) {
        logger.debug("Checking token validity");
        return tokenGenerator.isValid(token);
    }

    @Override
    public void invalidateToken(String token) {
        logger.debug("Invalidating token");
        // Implementation depends on token invalidation strategy
        // For JWT, tokens are self-contained and cannot be revoked
        // Could implement a blacklist mechanism if needed
    }
}
