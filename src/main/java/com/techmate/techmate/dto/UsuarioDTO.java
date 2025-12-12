package com.techmate.techmate.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO para transferencia de datos de usuarios (Usuario).
 * 
 * Campos con validaciones Jakarta para seguridad:
 * - Validaciones de email, nombres no vacíos
 * - Incluye soporte para roles como Set<String>
 * - Exponemos todos los campos necesarios de la base de datos (excepto password por seguridad)
 */
@Data
public class UsuarioDTO {

    @NotNull(message = "El ID del usuario no puede ser nulo")
    @Min(value = 1, message = "El ID del usuario debe ser mayor a 0")
    private Integer id;

    @NotBlank(message = "El nombre de usuario no puede estar vacío")
    private String username;

    @NotBlank(message = "El nombre no puede estar vacío")
    private String firstName;

    @NotBlank(message = "El apellido no puede estar vacío")
    private String lastName;

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "El email debe ser válido")
    private String email;

    private String profileImageUrl;

    private boolean isEnabled;

    private LocalDate birthDate;

    private String gender;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @NotNull(message = "Los roles no pueden ser nulos")
    private Set<String> roles;

    // ══════════════════════════════════════════════════════════════
    // ✅ COMPATIBLE METHODS (Legacy code support)
    // ══════════════════════════════════════════════════════════════
    /**
     * Método compatible para código legacy que usa 'id'.
     */
    public Integer getId() {
        return this.id;
    }

    /**
     * Método compatible para código legacy que usa 'id'.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Método compatible para código legacy que usa 'userName'.
     */
    public String getUserName() {
        return this.username;
    }

    /**
     * Método compatible para código legacy que usa 'userName'.
     */
    public void setUserName(String userName) {
        this.username = userName;
    }
}
