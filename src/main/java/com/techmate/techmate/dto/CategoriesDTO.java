package com.techmate.techmate.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para transferencia de datos de categorías (Categories).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriesDTO {
    
    @NotNull(message = "El ID de la categoría no puede ser nulo")
    @Min(value = 1, message = "El ID de la categoría debe ser mayor a 0")
    private int id;
    
    @NotBlank(message = "El nombre de la categoría no puede estar vacío")
    @Size(min = 3, max = 100, message = "El nombre de la categoría debe tener entre 3 y 100 caracteres")
    private String name;
    
    private String imagePath;
    
    // ✅ COMPATIBILITY METHOD
    public int getCategoryId() { return this.id; }
    public void setCategoryId(int categoryId) { this.id = categoryId; }
}

