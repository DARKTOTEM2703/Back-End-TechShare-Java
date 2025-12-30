package com.techmate.techmate.hexagonal.infrastructure.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordEncoderTest {

    public static void main(String[] args) {
        // Crear una instancia de PasswordEncoder
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // Probar el método de codificación
        String rawPassword = "root123";
        String encodedPassword = encodePassword(rawPassword, passwordEncoder);

        // Imprimir la contraseña codificada
        System.out.println("Contraseña original: " + rawPassword);
        System.out.println("Contraseña codificada: " + encodedPassword);
        
    }

    // Método que codifica la contraseña
    public static String encodePassword(String password, PasswordEncoder passwordEncoder) {
        return passwordEncoder.encode(password);
    }
}








