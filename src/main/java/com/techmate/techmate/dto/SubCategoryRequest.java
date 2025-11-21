package com.techmate.techmate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubCategoryRequest {

    private Integer id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 3, max = 100, message = "El nombre de la subcategoría debe tener entre 3 y 100 caracteres.")
    private String name;

    @NotNull(message = "La categoría asociada es obligatoria")
    private Integer categoryId;

}

