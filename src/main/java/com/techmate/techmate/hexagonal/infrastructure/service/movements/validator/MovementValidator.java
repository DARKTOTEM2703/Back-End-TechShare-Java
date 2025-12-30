package com.techmate.techmate.hexagonal.infrastructure.service.movements.validator;

import org.springframework.stereotype.Component;

import com.techmate.techmate.hexagonal.infrastructure.dto.MovementsDTO;

@Component
public class MovementValidator {

    public void validateQuantity(MovementsDTO dto) {
        if (dto.getQuantity() <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
    }
}








