package com.techmate.techmate.hexagonal.infrastructure.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para transferencia de datos de subcategorías (SubCategories).
 * Incluye validaciones Jakarta para integridad de datos.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubCategoriesDTO {

    @NotNull(message = "El ID de la subcategoría no puede ser nulo")
    @Min(value = 1, message = "El ID de la subcategoría debe ser mayor a 0")
    private int id;

    @NotBlank(message = "El nombre de la subcategoría no puede estar vacío")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String name;

    private String imagePath;

    @NotNull(message = "El ID de categoría no puede ser nulo")
    @Positive(message = "El ID de categoría debe ser positivo")
    private int categoryId;

    private String categoryName;

    // ✅ COMPATIBILITY METHOD
    public int getSubCategoriesId() {
        return this.id;
    }

    public void setSubCategoriesId(int subCategoriesId) {
        this.id = subCategoriesId;
    }

    // Constructores

}


