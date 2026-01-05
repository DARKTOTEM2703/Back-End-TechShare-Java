package com.techmate.techmate.infrastructure.dto;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowResponse {
    private int borrowId;
    private Date date;
    private Date startDate;
    private Date endDate;
    private Date returnDate;
    private com.techmate.techmate.infrastructure.persistence.entity.Status status;
    private java.math.BigDecimal amount;

    private int usuarioId;
    private String usuarioName;

    private int adminId;
    private String adminName;

    private List<DetailsBorrowResponse> details;
}







