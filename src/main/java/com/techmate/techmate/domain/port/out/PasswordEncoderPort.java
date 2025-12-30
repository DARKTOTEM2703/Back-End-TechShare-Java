package com.techmate.techmate.domain.port.out;

/**
 * 🔌 OUTPUT PORT - PasswordEncoderPort
 * 
 * Define el contrato para encriptación de contraseñas.
 * Abstrae la lógica de encriptación (BCrypt, etc.)
 * 
 * @author TechShare Team - Hexagonal Architecture
 * @version 2.0.0
 */
public interface PasswordEncoderPort {

    /**
     * Encripta una contraseña en plain text
     * 
     * @param plainPassword Contraseña sin encriptar
     * @return Contraseña hasheada
     */
    String encode(String plainPassword);

    /**
     * Verifica si una contraseña en plain text coincide con el hash
     * 
     * @param plainPassword Contraseña sin encriptar
     * @param encodedPassword Contraseña hasheada
     * @return true si coinciden, false en caso contrario
     */
    boolean matches(String plainPassword, String encodedPassword);
}
