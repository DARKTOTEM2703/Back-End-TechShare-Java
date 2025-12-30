package com.techmate.techmate.domain.port.in;

import java.util.List;

/**
 * Input port for authentication use cases.
 * 
 * Defines operations for user login, token generation, and validation.
 */
public interface AuthenticationUseCase {

    /**
     * Authenticate user with credentials.
     */
    AuthenticationResponse authenticate(String email, String plainPassword);

    /**
     * Validate token and extract claims.
     */
    boolean validateToken(String token);

    /**
     * Generate JWT token for user.
     */
    String generateToken(Integer userId, String username, String email, List<String> roles);

    /**
     * Check if token is still valid.
     */
    boolean isTokenValid(String token);

    /**
     * Invalidate token (logout).
     */
    void invalidateToken(String token);

    // ============= DTOs =============

    /**
     * Authentication response DTO.
     */
    record AuthenticationResponse(
            Integer userId,
            String username,
            String email,
            String token,
            List<String> roles,
            long expiresIn
    ) {}
}
