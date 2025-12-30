package com.techmate.techmate.hexagonal.domain.port.out;

/**
 * Output port for password encoding operations.
 * 
 * Defines the contract for password encoder implementations.
 * Isolates domain logic from cryptography infrastructure.
 */
public interface PasswordEncoderPort {

    /**
     * Encode a plaintext password.
     * 
     * @param plainPassword plaintext password to encode
     * @return encoded password hash
     */
    String encode(String plainPassword);

    /**
     * Check if plaintext password matches encoded password.
     * 
     * @param plainPassword plaintext password to check
     * @param encodedPassword encoded password hash to compare against
     * @return true if passwords match, false otherwise
     */
    boolean matches(String plainPassword, String encodedPassword);
}
