package com.techmate.techmate.hexagonal.infrastructure.dto;

import java.util.Date;
import java.util.List;

import com.techmate.techmate.hexagonal.domain.entity.Status;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO para transferencia de datos de préstamos (Borrow).
 * Incluye validaciones Jakarta para seguridad de datos.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BorrowDTO {

    @NotNull(message = "El ID del préstamo no puede ser nulo")
    @Min(value = 1, message = "El ID del préstamo debe ser mayor a 0")
    private int id;

    @NotNull(message = "La fecha de préstamo no puede ser nula")
    private Date date;

    private Date startDate;

    @NotNull(message = "La fecha de vencimiento no puede ser nula")
    private Date endDate;

    private Date returnDate;

    @NotNull(message = "El estado del préstamo no puede ser nulo")
    private Status status;

    @jakarta.validation.constraints.DecimalMin(value = "0.00", inclusive = true, message = "El monto no puede ser negativo")
    private java.math.BigDecimal amount;

    @NotNull(message = "El ID del usuario no puede ser nulo")
    @Min(value = 1, message = "El ID del usuario debe ser mayor a 0")
    private int usuarioId;

    private String usuarioName;

    private int adminId;
    private String adminName;

    @NotNull(message = "Los detalles del préstamo no pueden ser nulos")
    private List<DetailsBorrowDTO> details;

    // ══════════════════════════════════════════════════════════════
    // ✅ COMPATIBILITY METHODS (Legacy code support)
    // ══════════════════════════════════════════════════════════════
    public int getBorrowId() {
        return this.id;
    }

    public void setBorrowId(int borrowId) {
        this.id = borrowId;
    }
}


