package com.techmate.techmate.hexagonal.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerificationResponse {
    private boolean success;
    private String message;
}









