package com.techmate.techmate.hexagonal.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import java.math.BigDecimal;

@Entity
@Table(name = "details_borrow")
@Data
public class DetailsBorrow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id; // PK

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "unit_price", precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "total_price", precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @ManyToOne
    @JoinColumn(name = "material_id")
    private Materials materials; // Material asociado a este detalle

    @ManyToOne
    @JoinColumn(name = "borrow_id")
    @ToString.Exclude // ✅ Evita ciclo infinito con Borrow.toString()
    private Borrow borrow; // Préstamo asociado a este detalle

    // Compatibility getters/setters
    public Integer getDetailsBorrowId() {
        return this.id;
    }

    public void setDetailsBorrowId(Integer id) {
        this.id = id;
    }

    public Integer getMaterialsId() {
        return this.materials != null ? this.materials.getId() : null;
    }

    public Integer getBorrowId() {
        return this.borrow != null ? this.borrow.getId() : null;
    }
}







