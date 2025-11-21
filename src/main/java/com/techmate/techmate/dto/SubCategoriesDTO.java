package com.techmate.techmate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubCategoriesDTO {

    private int id;

    //@NotBlank(message = "El nombre no puede estar vacío") // Valida que el nombre no esté vacío
    //@Size(min = 3, max = 100, message = "El nombre de la subcategoría debe tener entre 3 y 100 caracteres.")
    private String name;
   
    
    private String imagePath;
    
    private int categoryId; // ID de la categoría a la que pertenece la subcategoría
    private String categoryName; // Nuevo campo para el nombre de la categoría

    // ✅ COMPATIBILITY METHOD
    public int getSubCategoriesId() { return this.id; }
    public void setSubCategoriesId(int subCategoriesId) { this.id = subCategoriesId; }

    // Constructores
   

}

