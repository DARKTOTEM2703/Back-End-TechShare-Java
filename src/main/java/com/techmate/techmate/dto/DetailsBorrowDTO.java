package com.techmate.techmate.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * DTO para detalles de préstamos (DetailsBorrow).
 * Incluye validaciones Jakarta para seguridad de datos.
 */
@Data
public class DetailsBorrowDTO {

    private Integer id; // Identificador del detalle del préstamo
    
    @NotNull(message = "La cantidad no puede ser nula")
    @Positive(message = "La cantidad debe ser mayor a 0")
    private Integer quantity; // Cantidad de materiales
    
    @NotNull(message = "El precio unitario no puede ser nulo")
    @Min(value = 0, message = "El precio unitario debe ser mayor o igual a 0")
    private double unitPrice; // Precio unitario del material
    
    @Min(value = 0, message = "El precio total debe ser mayor o igual a 0")
    private double totalPrice; // Precio total del detalle
    
    @NotNull(message = "El ID del material no puede ser nulo")
    @Positive(message = "El ID del material debe ser mayor a 0")
    private Integer materialsId; // ID del material asociado a este detalle
    
    private Integer borrowId; // ID del préstamo asociado a este detalle

    // ✅ COMPATIBILITY METHOD
    public Integer getDetailsBorrowId() {
        return this.id;
    }

    public void setDetailsBorrowId(Integer detailsBorrowId) {
        this.id = detailsBorrowId;
    }
}
