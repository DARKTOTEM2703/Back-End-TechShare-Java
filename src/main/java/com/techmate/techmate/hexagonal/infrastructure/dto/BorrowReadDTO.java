package com.techmate.techmate.hexagonal.infrastructure.dto;

import java.util.Date;
import java.util.List;

import com.techmate.techmate.hexagonal.domain.entity.Status;
import lombok.Data;

/**
 * DTO para LEER préstamos (read operations).
 * Incluye objetos completos y datos calculados.
 * Usado en endpoints GET.
 */
@Data
public class BorrowReadDTO {

    /**
     * ID del préstamo
     */
    private Integer id;

    /**
     * Fecha de creación del préstamo
     */
    private Date date;

    /**
     * Fecha de inicio efectiva del préstamo (cuando fue aprobado)
     */
    private Date startDate;

    /**
     * Fecha de vencimiento
     */
    private Date endDate;

    /**
     * Fecha de devolución real (si ya fue devuelto)
     */
    private Date returnDate;

    /**
     * Estado actual del préstamo
     */
    private Status status;

    /**
     * Monto total del préstamo
     */
    private java.math.BigDecimal amount;

    /**
     * ID del usuario solicitante
     */
    private Integer usuarioId;

    /**
     * Nombre del usuario solicitante
     */
    private String usuarioName;

    /**
     * Email del usuario solicitante
     */
    private String usuarioEmail;

    /**
     * ID del administrador que procesó el préstamo
     */
    private Integer adminId;

    /**
     * Nombre del administrador
     */
    private String adminName;

    /**
     * Detalles del préstamo
     */
    private List<DetailsBorrowDTO> details;

    /**
     * Material relacionado (si aplica)
     */
    private Integer materialId;

    private String materialName;

    // ══════════════════════════════════════════════════════════════
    // ✅ COMPATIBILITY METHOD (Legacy code support)
    // ══════════════════════════════════════════════════════════════
    /**
     * Método compatible para código legacy que usa 'borrowId'.
     */
    public Integer getBorrowId() {
        return this.id;
    }

    public void setBorrowId(Integer borrowId) {
        this.id = borrowId;
    }
}






