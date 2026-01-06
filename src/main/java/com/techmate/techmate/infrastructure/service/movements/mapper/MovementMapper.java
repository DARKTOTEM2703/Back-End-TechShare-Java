package com.techmate.techmate.infrastructure.service.movements.mapper;

import org.springframework.stereotype.Component;

import com.techmate.techmate.infrastructure.dto.MovementsDTO;
import com.techmate.techmate.infrastructure.persistence.entity.Materials;
import com.techmate.techmate.infrastructure.persistence.entity.Movements;
import com.techmate.techmate.infrastructure.persistence.entity.Usuario;

@Component
public class MovementMapper {

    public Movements toEntity(MovementsDTO dto, Usuario usuario, Materials materials) {
        if (dto == null)
            return null;
        Movements m = new Movements();
        m.setId(dto.getId());
        // Convert domain MoveType to JPA MoveType
        if (dto.getMoveType() != null) {
            m.setMoveType(
                    com.techmate.techmate.infrastructure.persistence.entity.MoveType.valueOf(dto.getMoveType().name()));
        }
        m.setQuantity(dto.getQuantity());
        m.setDate(dto.getDate());
        m.setComment(dto.getComment());
        m.setUsuario(usuario);
        m.setMaterials(materials);
        return m;
    }

    public MovementsDTO toDTO(Movements m, String usuarioName, String materialName) {
        if (m == null)
            return null;
        MovementsDTO dto = new MovementsDTO();
        dto.setId(m.getId());
        // Convert JPA MoveType to domain MoveType
        if (m.getMoveType() != null) {
            dto.setMoveType(com.techmate.techmate.core.domain.model.movement.MoveType.valueOf(m.getMoveType().name()));
        }
        dto.setQuantity(m.getQuantity());
        dto.setDate(m.getDate());
        dto.setComment(m.getComment());
        dto.setAdminId(m.getUsuario().getId());
        dto.setId(m.getMaterials().getId());
        dto.setAdminName(usuarioName);
        dto.setMaterialsName(materialName);
        return dto;
    }
}
