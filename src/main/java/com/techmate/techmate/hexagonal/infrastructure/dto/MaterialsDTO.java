package com.techmate.techmate.hexagonal.infrastructure.dto;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.techmate.techmate.hexagonal.infrastructure.validation.SafeString;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para transferencia de datos de materiales (Materials).
 * Incluye validaciones Jakarta para seguridad de datos.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaterialsDTO {

    @NotNull(message = "El ID del material no puede ser nulo")
    @Min(value = 1, message = "El ID del material debe ser mayor a 0")
    private int id;

    private String imagePath;

    @NotBlank(message = "El nombre del material no puede estar vacío")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    @SafeString(allowSpecial = false)
    private String name;

    @NotBlank(message = "La descripción del material no puede estar vacía")
    @Size(min = 5, max = 500, message = "La descripción debe tener entre 5 y 500 caracteres")
    @SafeString(allowSpecial = true)
    private String description;

    @NotNull(message = "El precio no puede ser nulo")
    @jakarta.validation.constraints.DecimalMin(value = "0.00", inclusive = true, message = "El precio debe ser mayor o igual a 0")
    private BigDecimal price;

    @NotNull(message = "El stock no puede ser nulo")
    @Min(value = 0, message = "El stock debe ser mayor o igual a 0")
    private int stock;

    @NotNull(message = "El stock prestable no puede ser nulo")
    @Min(value = 0, message = "El stock prestable debe ser mayor o igual a 0")
    private int borrowableStock;

    @NotNull(message = "El ID de la subcategoría no puede ser nulo")
    @Min(value = 1, message = "El ID de la subcategoría debe ser mayor a 0")
    private int subCategoryId;

    private String subCategoryName;

    private List<Integer> roleIds;
    private List<String> roleNames;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // ✅ COMPATIBILITY METHOD
    public int getMaterialsId() {
        return this.id;
    }

    public void setMaterialsId(int materialsId) {
        this.id = materialsId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getBorrowableStock() {
        return borrowableStock;
    }

    public void setBorrowableStock(int borrowableStock) {
        this.borrowableStock = borrowableStock;
    }

    public int getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(int subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }
}


