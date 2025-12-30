package com.techmate.techmate.hexagonal.infrastructure.service.movements.mapper;

import org.springframework.stereotype.Component;

import com.techmate.techmate.hexagonal.infrastructure.dto.MovementResponse;
import com.techmate.techmate.hexagonal.infrastructure.dto.MovementsDTO;

@Component
public class MovementsMapper {

    public MovementResponse toResponse(MovementsDTO dto) {
        if (dto == null) return null;
        MovementResponse resp = new MovementResponse();
        resp.setId(dto.getId());
        resp.setMoveType(dto.getMoveType());
        resp.setQuantity(dto.getQuantity());
        resp.setDate(dto.getDate());
        resp.setComment(dto.getComment());
        resp.setAdminId(dto.getAdminId());
        resp.setAdminName(dto.getAdminName());
        resp.setId(dto.getId());
        resp.setMaterialsName(dto.getMaterialsName());
        return resp;
    }
}









