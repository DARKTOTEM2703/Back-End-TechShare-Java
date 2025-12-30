package com.techmate.techmate.infrastructure.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.techmate.techmate.infrastructure.validation.SafeString;

/**
 * DTO usado para solicitudes (create/update) de categorías. Solo contiene
 * los campos aceptados desde el cliente.
 */
public class CategoryRequest {

    @NotBlank(message = "El nombre de la categoría no puede estar vacío.")
    @Size(min = 3, max = 100, message = "El nombre de la categoría debe tener entre 3 y 100 caracteres.")
    @SafeString(allowSpecial = false)
    private String name;

    public CategoryRequest() {
    }

    public CategoryRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}







