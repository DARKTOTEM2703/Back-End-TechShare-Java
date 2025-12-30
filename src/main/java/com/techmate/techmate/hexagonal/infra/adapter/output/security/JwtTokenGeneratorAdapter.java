package com.techmate.techmate.hexagonal.infra.adapter.output.security;

import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techmate.techmate.hexagonal.domain.port.out.TokenGeneratorPort;
import com.techmate.techmate.security.TokenUtils;
import com.techmate.techmate.service.TokenService;

import java.util.List;

/**
 * JWT token generator adapter using existing TokenUtils and TokenService.
 * 
 * Implements TokenGeneratorPort to provide JWT token generation and validation.
 * Integrates with existing TokenUtils and TokenService infrastructure.
 */
@Component
public class JwtTokenGeneratorAdapter implements TokenGeneratorPort {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenGeneratorAdapter.class);

    private final TokenService tokenService;

    public JwtTokenGeneratorAdapter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public String generateToken(Integer userId, String username, String email, List<String> roles) {
        logger.debug("Generating JWT token for user: {} ({})", username, userId);
        
        if (userId == null || username == null || email == null) {
            throw new IllegalArgumentException("User ID, username, and email cannot be null");
        }
        
        // Convert String roles to Integer role IDs (using placeholder; adjust as needed)
        List<Integer> roleIds = roles != null ? roles.stream()
                .map(role -> {
                    // Simple mapping: ROLE_USER -> 1, ROLE_ADMIN -> 2, ROLE_MODERATOR -> 3
                    return switch (role.toUpperCase()) {
                        case "ADMIN" -> 2;
                        case "MODERATOR" -> 3;
                        default -> 1;  // Default to USER
                    };
                })
                .toList() : List.of();
        
        return TokenUtils.createToken(userId, email, username, roles, roleIds);
    }

    @Override
    public Integer extractUserId(String token) {
        logger.debug("Extracting user ID from token");
        return tokenService.getUserIdFromToken(token);
    }

    @Override
    public String extractUsername(String token) {
        logger.debug("Extracting username from token");
        return tokenService.getUserNameFromToken(token);
    }

    @Override
    public List<String> extractRoles(String token) {
        logger.debug("Extracting roles from token");
        return tokenService.getRolesFromToken(token)
                .map(roleIds -> roleIds.stream()
                        .map(roleId -> switch (roleId) {
                            case 2 -> "ADMIN";
                            case 3 -> "MODERATOR";
                            default -> "USER";
                        })
                        .toList())
                .orElse(List.of());
    }

    @Override
    public boolean isValid(String token) {
        logger.debug("Validating JWT token");
        try {
            // If decodeToken succeeds without exception, token is valid
            TokenUtils.decodeToken(token);
            return true;
        } catch (Exception e) {
            logger.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }
}
