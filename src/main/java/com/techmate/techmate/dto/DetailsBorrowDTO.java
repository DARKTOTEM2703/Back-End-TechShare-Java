package com.techmate.techmate.dto;

import lombok.Data;

@Data
public class DetailsBorrowDTO {

    private Integer id; // Identificador del detalle del préstamo
    private Integer quantity; // Cantidad de materiales
    private double unitPrice; // Precio unitario del material
    private double totalPrice; // Precio total del detalle
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
