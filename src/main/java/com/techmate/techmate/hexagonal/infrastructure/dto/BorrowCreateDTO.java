package com.techmate.techmate.hexagonal.infrastructure.dto;

import java.util.Date;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO para CREAR préstamos (write operations).
 * Solo contiene IDs de entidades relacionadas, no objetos completos.
 * Usado en endpoints POST/PUT.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BorrowCreateDTO {

    /**
     * ID del préstamo (null para creación, presente para actualización)
     */
    private Integer id;

    /**
     * Fecha de vencimiento del préstamo
     */
    @NotNull(message = "La fecha de vencimiento es obligatoria")
    @Future(message = "La fecha de vencimiento debe ser futura")
    private Date endDate;

    /**
     * Monto total del préstamo
     */
    @jakarta.validation.constraints.DecimalMin(value = "0.00", inclusive = true, message = "El monto no puede ser negativo")
    private java.math.BigDecimal amount;

    /**
     * ID del usuario que solicita el préstamo
     */
    @NotNull(message = "El ID del usuario es obligatorio")
    @Min(value = 1, message = "El ID del usuario debe ser mayor a 0")
    private Integer usuarioId;

    /**
     * Detalles del préstamo (materiales y cantidades)
     */
    @NotNull(message = "Los detalles del préstamo son obligatorios")
    @NotEmpty(message = "Debe incluir al menos un material")
    @Valid
    private List<DetailsBorrowDTO> details;
}







