package com.techmate.techmate.domain.port.in;

import com.techmate.techmate.domain.model.user.User;

/**
 * 🎯 INPUT PORT - AuthenticationUseCase
 * 
 * Caso de uso para autenticación de usuarios.
 * Maneja login, logout, validación de credenciales, etc.
 * 
 * @author TechShare Team - Hexagonal Architecture
 * @version 2.0.0
 */
public interface AuthenticationUseCase {

    /**
     * Autentica un usuario con usuario/email y contraseña
     * Retorna un token JWT en caso de éxito
     * 
     * @throws IllegalArgumentException si las credenciales son inválidas
     */
    AuthenticationResponse authenticate(String usernameOrEmail, String password);

    /**
     * Valida un token JWT y retorna la información del usuario
     * 
     * @throws IllegalArgumentException si el token es inválido o ha expirado
     */
    User validateToken(String token);

    /**
     * Genera un token JWT para un usuario (uso interno)
     */
    String generateToken(User user);

    /**
     * Verifica si un token es válido
     */
    boolean isTokenValid(String token);

    /**
     * Invalida un token (logout)
     */
    void invalidateToken(String token);

    // ═══ DTOs ═══

    record AuthenticationResponse(
        String token,
        User user,
        String tokenType
    ) {}
}
