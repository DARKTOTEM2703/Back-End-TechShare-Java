package com.techmate.techmate.hexagonal.infrastructure.dto;

import java.util.Date;

import com.techmate.techmate.hexagonal.domain.entity.MoveType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovementResponse {
    private int id;
    private MoveType moveType;
    private int quantity;
    private Date date;
    private String comment;

    private int adminId;
    private String adminName;

    private int materialsId;
    private String materialsName;
}






