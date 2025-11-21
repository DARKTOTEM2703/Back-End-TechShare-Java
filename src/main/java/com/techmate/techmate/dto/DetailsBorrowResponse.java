package com.techmate.techmate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailsBorrowResponse {
    private Integer detailsBorrowId;
    private Integer quantity;
    private double unitPrice;
    private double totalPrice;
    private Integer materialsId;
}

