package com.techmate.techmate.hexagonal.infrastructure.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

import com.techmate.techmate.hexagonal.domain.entity.Usuario.Gender;

@Data
public class RegisterRequest {
    
    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "El nombre de usuario solo puede contener letras, números, guiones bajos y guiones")
    private String user_name;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s'-]+$", message = "El nombre contiene caracteres inválidos")
    private String first_name;
    
    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 100, message = "El apellido debe tener entre 2 y 100 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s'-]+$", message = "El apellido contiene caracteres inválidos")
    private String last_name;
    
    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "Formato de correo electrónico inválido", regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    @Size(max = 255, message = "El correo electrónico debe tener menos de 255 caracteres")
    private String email;
    
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 128, message = "La contraseña debe tener entre 8 y 128 caracteres")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&_\\-#.,:;])[A-Za-z\\d@$!%*?&_\\-#.,:;]{8,}$",
        message = "La contraseña debe contener al menos una mayúscula, una minúscula, un número y un carácter especial (@$!%*?&_-#.,:;)"
    )
    private String password;
    
    // Fecha de nacimiento (opcional)
    @Past(message = "La fecha de nacimiento debe ser anterior a hoy")
    private LocalDate birthDate;
    
    // Género (opcional)
    private Gender gender;
    
    // Roles es opcional - si no se envía, se asignará el rol por defecto
    private Set<@Min(value = 1, message = "ID de rol inválido") Integer> roles;
}








