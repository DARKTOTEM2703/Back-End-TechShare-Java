package com.techmate.techmate.dto;

import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO para detalles de préstamos (DetailsBorrow).
 * Incluye validaciones Jakarta para seguridad de datos.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetailsBorrowDTO {

    private Integer id; // Identificador del detalle del préstamo

    @NotNull(message = "La cantidad no puede ser nula")
    @Positive(message = "La cantidad debe ser mayor a 0")
    private Integer quantity; // Cantidad de materiales

    @NotNull(message = "El precio unitario no puede ser nulo")
    @jakarta.validation.constraints.DecimalMin(value = "0.00", inclusive = true, message = "El precio unitario debe ser mayor o igual a 0")
    private BigDecimal unitPrice; // Precio unitario del material

    @jakarta.validation.constraints.DecimalMin(value = "0.00", inclusive = true, message = "El precio total debe ser mayor o igual a 0")
    private BigDecimal totalPrice; // Precio total del detalle

    @NotNull(message = "El ID del material no puede ser nulo")
    @Positive(message = "El ID del material debe ser mayor a 0")
    private Integer materialsId; // ID del material asociado a este detalle

    private Integer borrowId; // ID del préstamo asociado a este detalle

    /**
     * Nombre del material asociado a este detalle (enriquecido para lectura)
     */
    private String materialName;

    // ✅ COMPATIBILITY METHOD
    public Integer getDetailsBorrowId() {
        return this.id;
    }

    public void setDetailsBorrowId(Integer detailsBorrowId) {
        this.id = detailsBorrowId;
    }
}
