package com.techmate.techmate.hexagonal.domain.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "movements")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movements {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type")
    private MoveType moveType;

    @Column(name = "resource_id")
    private Integer resourceId;

    @Column(name = "notes")
    private String notes;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "movement_date")
    private Date movementDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id", nullable = false, insertable = false, updatable = false)
    private Materials materials;

    // Legacy fields for backward compatibility (not mapped to DB)
    @Transient
    private int quantity;
    
    @Transient
    private String comment;
    
    @Transient
    private Date date;

    // Compatibility getters/setters for legacy code
    public int getMovementsId() { 
        return this.id; 
    }
    
    public void setMovementsId(int id) { 
        this.id = id; 
    }
    
    public Materials getMaterials() {
        return this.materials;
    }
    
    public void setMaterials(Materials materials) {
        this.materials = materials;
    }
    
    public int getQuantity() {
        return this.quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public String getComment() {
        return this.comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public Date getDate() {
        return this.date != null ? this.date : this.movementDate;
    }
    
    public void setDate(Date date) {
        this.date = date;
        this.movementDate = date;
    }
}






