package com.techmate.techmate.hexagonal.domain.port.out;

import java.util.List;

/**
 * Output port for JWT token generation and validation.
 * 
 * Defines the contract for token generator implementations.
 * Isolates domain logic from authentication infrastructure (JWT, OAuth, etc.).
 */
public interface TokenGeneratorPort {

    /**
     * Generate JWT token for user.
     * 
     * @param userId user ID
     * @param username username
     * @param email user email
     * @param roles list of role names
     * @return JWT token string
     */
    String generateToken(Integer userId, String username, String email, List<String> roles);

    /**
     * Extract user ID from token.
     * 
     * @param token JWT token
     * @return user ID or null if invalid
     */
    Integer extractUserId(String token);

    /**
     * Extract username from token.
     * 
     * @param token JWT token
     * @return username or null if invalid
     */
    String extractUsername(String token);

    /**
     * Extract role names from token.
     * 
     * @param token JWT token
     * @return list of role names
     */
    List<String> extractRoles(String token);

    /**
     * Validate token signature and expiration.
     * 
     * @param token JWT token
     * @return true if valid, false otherwise
     */
    boolean isValid(String token);
}
