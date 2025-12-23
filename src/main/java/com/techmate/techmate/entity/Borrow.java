package com.techmate.techmate.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import java.util.Date;
import java.util.List;

/**
 * Entidad Borrow - Representa un préstamo de material.
 */
@Entity
@Table(name = "borrow")
@Data
public class Borrow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Temporal(TemporalType.DATE)
    @Column(name = "borrow_date")
    private Date date;

    @Temporal(TemporalType.DATE)
    @Column(name = "due_date")
    private Date endDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "return_date")
    private Date returnDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @Column(name = "resource_id")
    private Integer resourceId;

    private java.math.BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Usuario usuario;

    // Legacy compatibility fields (not persisted in DB - @Transient)
    @Transient
    private Date startDate;

    @Transient
    private Usuario admin;

    @OneToMany(mappedBy = "borrow", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude // ✅ Evita ciclo infinito con DetailsBorrow.toString()
    private List<DetailsBorrow> details;

    // ══════════════════════════════════════════════════════════════
    // ✅ COMPATIBLE METHODS (Legacy code support - borrowId)
    // ══════════════════════════════════════════════════════════════

    /**
     * Método compatible para código legacy que usa 'borrowId'.
     * Internamente mapea a 'id'.
     */
    public Integer getBorrowId() {
        return this.id;
    }

    /**
     * Método compatible para código legacy que usa 'borrowId'.
     * Internamente mapea a 'id'.
     */
    public void setBorrowId(Integer borrowId) {
        this.id = borrowId;
    }

    // ══════════════════════════════════════════════════════════════
    // BUSINESS METHODS
    // ══════════════════════════════════════════════════════════════

    /**
     * Calcula el monto total del préstamo sumando los detalles.
     * 
     * @return Suma de los precios totales de todos los detalles del préstamo
     */
    public java.math.BigDecimal calculateTotalAmount() {
        if (details != null && !details.isEmpty()) {
            return details.stream()
                    .map(DetailsBorrow::getTotalPrice)
                    .filter(java.util.Objects::nonNull)
                    .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        }
        return java.math.BigDecimal.ZERO;
    }
}
