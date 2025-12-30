package com.techmate.techmate.hexagonal.application.mapper;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.techmate.techmate.hexagonal.domain.entity.Movements;
import com.techmate.techmate.hexagonal.domain.entity.Materials;
import com.techmate.techmate.hexagonal.domain.entity.Usuario;
import com.techmate.techmate.hexagonal.domain.model.movement.Movement;
import com.techmate.techmate.hexagonal.domain.model.movement.MoveType;
import com.techmate.techmate.hexagonal.domain.port.input.MovementManagementUseCase;

/**
 * DomainMovementMapper - Manual mapper for Movement domain model ↔ JPA/DTO
 * conversion.
 * Handles conversions between:
 * - Domain Movement model (immutable, pure business logic)
 * - JPA Movements entity (database persistence)
 * - DTOs (API input/output)
 * 
 * No MapStruct used - explicit mappings for clarity and control.
 * 
 * @author TechShare Team
 * @version 1.0
 */
@Component
public class DomainMovementMapper {

    /**
     * Convert JPA Movements entity to domain Movement model
     */
    public Movement toDomain(Movements jpaMovement) {
        if (jpaMovement == null) {
            return null;
        }

        return new Movement.Builder()
                .id(jpaMovement.getId())
                .moveType(convertJpaMoveType(jpaMovement.getMoveType()))
                .resourceId(jpaMovement.getResourceId())
                .notes(jpaMovement.getNotes())
                .movementDate(jpaMovement.getMovementDate())
                .userId(jpaMovement.getUsuario() != null ? jpaMovement.getUsuario().getId() : null)
                .materialId(jpaMovement.getMaterials() != null ? jpaMovement.getMaterials().getId() : null)
                .quantity(jpaMovement.getQuantity())
                .build();
    }

    /**
     * Convert domain Movement model to JPA Movements entity
     */
    public Movements toJpaEntity(Movement movement, Usuario usuario, Materials materials) {
        if (movement == null) {
            return null;
        }

        Movements jpaMovement = new Movements();
        jpaMovement.setId(movement.getId() != null ? movement.getId() : 0);
        jpaMovement.setMoveType(convertDomainMoveType(movement.getMoveType()));
        jpaMovement.setResourceId(movement.getResourceId());
        jpaMovement.setNotes(movement.getNotes());
        jpaMovement.setMovementDate(movement.getMovementDate());
        jpaMovement.setUsuario(usuario);
        jpaMovement.setMaterials(materials);
        jpaMovement.setQuantity(movement.getQuantity());

        return jpaMovement;
    }

    /**
     * Convert CreateMovementRequest to domain Movement model
     */
    public Movement toDomain(MovementManagementUseCase.CreateMovementRequest request) {
        if (request == null) {
            return null;
        }

        return new Movement.Builder()
                .moveType(request.getMoveType())
                .resourceId(request.getMaterialId())
                .notes(request.getNotes())
                .movementDate(new Date())
                .userId(request.getUserId())
                .materialId(request.getMaterialId())
                .quantity(request.getQuantity())
                .build();
    }

    /**
     * Update domain Movement with UpdateMovementRequest data
     */
    public Movement updateFromRequest(Movement existing, MovementManagementUseCase.UpdateMovementRequest request) {
        if (existing == null || request == null) {
            return existing;
        }

        return new Movement.Builder()
                .id(existing.getId())
                .moveType(request.getMoveType() != null ? request.getMoveType() : existing.getMoveType())
                .resourceId(existing.getResourceId())
                .notes(request.getNotes() != null ? request.getNotes() : existing.getNotes())
                .movementDate(existing.getMovementDate())
                .userId(existing.getUserId())
                .materialId(existing.getMaterialId())
                .quantity(request.getQuantity() > 0 ? request.getQuantity() : existing.getQuantity())
                .build();
    }

    /**
     * Convert domain Movement to MovementResponse DTO
     */
    public MovementManagementUseCase.HexMovementResponse toResponse(Movement movement) {
        if (movement == null) {
            return null;
        }

        MovementManagementUseCase.HexMovementResponse response = new MovementManagementUseCase.HexMovementResponse();
        response.setId(movement.getId());
        response.setMoveType(movement.getMoveType());
        response.setMaterialId(movement.getMaterialId());
        response.setUserId(movement.getUserId());
        response.setNotes(movement.getNotes());
        response.setMovementDate(movement.getMovementDate());
        response.setQuantity(movement.getQuantity());

        return response;
    }

    /**
     * Convert JPA MoveType enum to domain MoveType enum
     */
    private MoveType convertJpaMoveType(com.techmate.techmate.hexagonal.domain.entity.MoveType jpaMoveType) {
        if (jpaMoveType == null) {
            return null;
        }

        switch (jpaMoveType) {
            case BORROW:
                return MoveType.BORROW;
            case RETURN:
                return MoveType.RETURN;
            case STOCK_ADD:
                return MoveType.STOCK_ADD;
            case ADJUSTMENT:
                return MoveType.ADJUSTMENT;
            default:
                return null;
        }
    }

    /**
     * Convert domain MoveType enum to JPA MoveType enum
     */
    private com.techmate.techmate.hexagonal.domain.entity.MoveType convertDomainMoveType(MoveType domainMoveType) {
        if (domainMoveType == null) {
            return null;
        }

        switch (domainMoveType) {
            case BORROW:
                return com.techmate.techmate.hexagonal.domain.entity.MoveType.BORROW;
            case RETURN:
                return com.techmate.techmate.hexagonal.domain.entity.MoveType.RETURN;
            case STOCK_ADD:
                return com.techmate.techmate.hexagonal.domain.entity.MoveType.STOCK_ADD;
            case ADJUSTMENT:
                return com.techmate.techmate.hexagonal.domain.entity.MoveType.ADJUSTMENT;
            default:
                return null;
        }
    }
}
