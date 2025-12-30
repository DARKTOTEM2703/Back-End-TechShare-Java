package com.techmate.techmate.hexagonal.infrastructure.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.techmate.techmate.hexagonal.infrastructure.validation.SafeString;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para transferencia de datos de roles (Role).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleDTO {

    @NotNull(message = "El ID del rol no puede ser nulo")
    @Min(value = 1, message = "El ID del rol debe ser mayor a 0")
    private int id;

    @NotBlank(message = "El nombre del rol no puede estar vacío")
    @Size(min = 3, max = 50, message = "El nombre del rol debe tener entre 3 y 50 caracteres")
    @SafeString(allowSpecial = false)
    private String name;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    @SafeString(allowSpecial = true)
    private String description;

    // ✅ COMPATIBILITY METHOD
    public int getRoleId() {
        return this.id;
    }

    public void setRoleId(int roleId) {
        this.id = roleId;
    }
}






