package com.techmate.techmate.application.usecase.user;

import com.techmate.techmate.domain.model.user.User;
import com.techmate.techmate.domain.port.in.AuthenticationUseCase;
import com.techmate.techmate.domain.port.out.UserRepositoryPort;
import com.techmate.techmate.domain.port.out.PasswordEncoderPort;
import com.techmate.techmate.domain.port.out.TokenGeneratorPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 🎯 USE CASE - AuthenticationUseCaseImpl
 * 
 * Implementa la autenticación de usuarios.
 * Valida credenciales y genera tokens JWT.
 * 
 * @author TechShare Team - Hexagonal Architecture
 * @version 2.0.0
 */
@Service
@Transactional
public class AuthenticationUseCaseImpl implements AuthenticationUseCase {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationUseCaseImpl.class);

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
    @Transactional(readOnly = true)
    public AuthenticationResponse authenticate(String usernameOrEmail, String password) {
        log.info("🔐 Intentando autenticar: {}", usernameOrEmail);

        if (usernameOrEmail == null || usernameOrEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("Usuario o email no puede estar vacío");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }

        // Buscar usuario por username o email
        User user = userRepository.findByUsername(usernameOrEmail)
            .or(() -> userRepository.findByEmail(usernameOrEmail))
            .orElseThrow(() -> new IllegalArgumentException("Usuario o email no encontrado"));

        // Validar que el usuario está habilitado
        if (!user.isEnabled()) {
            log.warn("❌ Usuario deshabilitado intentó autenticarse: {}", usernameOrEmail);
            throw new IllegalArgumentException("El usuario está deshabilitado");
        }

        // Validar contraseña
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            log.warn("❌ Contraseña incorrecta para: {}", usernameOrEmail);
            throw new IllegalArgumentException("Contraseña incorrecta");
        }

        // Generar token
        String roles = String.join(",", user.getRoleNames());
        String token = tokenGenerator.generateToken(user.getId(), user.getUsername(), roles);

        log.info("✅ Usuario autenticado exitosamente: {}", user.getId());
        return new AuthenticationResponse(token, user, "Bearer");
    }

    @Override
    @Transactional(readOnly = true)
    public User validateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token no puede estar vacío");
        }

        if (!tokenGenerator.isValid(token)) {
            throw new IllegalArgumentException("Token inválido o expirado");
        }

        Integer userId = tokenGenerator.extractUserId(token);
        return userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }

    @Override
    public String generateToken(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Usuario no puede ser null");
        }

        String roles = String.join(",", user.getRoleNames());
        return tokenGenerator.generateToken(user.getId(), user.getUsername(), roles);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isTokenValid(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        return tokenGenerator.isValid(token);
    }

    @Override
    public void invalidateToken(String token) {
        log.info("🚪 Invalidando token (logout)");
        // En una implementación real, guardaríamos el token en una lista negra
        // Por ahora, solo registramos el logout
    }
}
