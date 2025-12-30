package com.techmate.techmate.hexagonal.infrastructure.security;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.techmate.techmate.hexagonal.infrastructure.dto.RegisterRequest;
import com.techmate.techmate.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthService authService, PasswordEncoder passwordEncoder) {
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @RequestBody RegisterRequest registerRequest) {
        
        // Validar que el objeto no sea nulo
        if (registerRequest == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "El cuerpo de la solicitud no puede estar vacío"));
        }
        
        try {
            // Sanitizar inputs adicionales (defensa en profundidad)
            registerRequest.setUser_name(sanitizeInput(registerRequest.getUser_name()));
            registerRequest.setFirst_name(sanitizeInput(registerRequest.getFirst_name()));
            registerRequest.setLast_name(sanitizeInput(registerRequest.getLast_name()));
            registerRequest.setEmail(registerRequest.getEmail().toLowerCase().trim());
            
            // Si no se envían roles, asignar rol de usuario por defecto (ID 2)
            if (registerRequest.getRoles() == null || registerRequest.getRoles().isEmpty()) {
                registerRequest.setRoles(Set.of(2)); // Rol de usuario normal
                log.info("No se proporcionaron roles, asignando rol por defecto (USUARIO)");
            } else {
                // Validar roles permitidos (prevenir escalada de privilegios)
                if (registerRequest.getRoles().stream().anyMatch(roleId -> roleId > 2)) {
                    log.warn("Intento de registro con rol no autorizado: {}", registerRequest.getRoles());
                    return ResponseEntity.badRequest().body(Map.of("error", "Selección de rol inválida"));
                }
            }
            
            // Encode password
            registerRequest.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            
            String msg = authService.registerUser(registerRequest);
            log.info("User registered successfully: {}", registerRequest.getEmail());
            return ResponseEntity.ok(Map.of("message", msg));
            
        } catch (IllegalArgumentException e) {
            log.warn("Registro fallido: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado durante el registro", e);
            return ResponseEntity.status(500).body(Map.of("error", "Error interno del servidor"));
        }
    }

    /**
     * Reenvía el email de verificación cuando el token ha expirado o se requiere uno nuevo.
     * Recibe JSON: { "email": "user@example.com" }
     */
    @PostMapping("/resend")
    public ResponseEntity<?> resendVerification(@RequestBody Map<String, String> body) {
        if (body == null || !body.containsKey("email") || body.get("email") == null || body.get("email").isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El correo electrónico es requerido"));
        }
        String email = body.get("email").toLowerCase().trim();
        try {
            String msg = authService.resendVerification(email);
            return ResponseEntity.ok(Map.of("message", msg));
        } catch (IllegalArgumentException e) {
            log.warn("Reenvío de verificación fallido: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado durante reenvío de verificación", e);
            return ResponseEntity.status(500).body(Map.of("error", "Error interno del servidor"));
        }
    }
    
    /**
     * Sanitiza input removiendo caracteres peligrosos
     */
    private String sanitizeInput(String input) {
        if (input == null) return null;
        return input.trim()
                .replaceAll("[<>\"'&]", "") // Remover caracteres HTML peligrosos
                .replaceAll("\\p{Cntrl}", ""); // Remover caracteres de control
    }
}




