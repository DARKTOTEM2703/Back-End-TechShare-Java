package com.techmate.techmate.domain.port.out;

/**
 * 🔌 OUTPUT PORT - TokenGeneratorPort
 * 
 * Define el contrato para generación y validación de tokens JWT.
 * 
 * @author TechShare Team - Hexagonal Architecture
 * @version 2.0.0
 */
public interface TokenGeneratorPort {

    /**
     * Genera un token JWT para un usuario
     * 
     * @param userId ID del usuario
     * @param username Nombre de usuario
     * @param roles Roles del usuario (comma-separated o lista)
     * @return Token JWT
     */
    String generateToken(Integer userId, String username, String roles);

    /**
     * Extrae el usuario ID de un token JWT
     * 
     * @throws IllegalArgumentException si el token es inválido
     */
    Integer extractUserId(String token);

    /**
     * Extrae el nombre de usuario de un token JWT
     * 
     * @throws IllegalArgumentException si el token es inválido
     */
    String extractUsername(String token);

    /**
     * Extrae los roles de un token JWT
     * 
     * @throws IllegalArgumentException si el token es inválido
     */
    String extractRoles(String token);

    /**
     * Verifica si un token es válido
     * 
     * @return true si es válido, false si ha expirado o es malformado
     */
    boolean isValid(String token);
}
