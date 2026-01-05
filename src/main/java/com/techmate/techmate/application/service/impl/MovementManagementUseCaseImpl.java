package com.techmate.techmate.application.service.impl;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techmate.techmate.core.domain.model.movement.Movement;
import com.techmate.techmate.core.domain.model.movement.MoveType;
import com.techmate.techmate.domain.port.input.MovementManagementUseCase;
import com.techmate.techmate.domain.port.output.MovementRepositoryPort;
import com.techmate.techmate.core.application.mapper.DomainMovementMapper;

/**
 * MovementManagementUseCaseImpl - Application service implementing movement management operations.
 * Orchestrates domain logic with infrastructure through port abstractions.
 * 
 * Responsibilities:
 * - Business logic validation for movements
 * - Coordination of repository operations
 * - Transaction management for consistency
 * - Logging and error handling
 * 
 * @author TechShare Team
 * @version 1.0
 */
@Service
@Transactional
public class MovementManagementUseCaseImpl implements MovementManagementUseCase {

    private static final Logger log = LoggerFactory.getLogger(MovementManagementUseCaseImpl.class);

    private final MovementRepositoryPort movementRepository;
    private final DomainMovementMapper movementMapper;

    public MovementManagementUseCaseImpl(
            MovementRepositoryPort movementRepository,
            DomainMovementMapper movementMapper) {
        this.movementRepository = movementRepository;
        this.movementMapper = movementMapper;
    }

    // ==================== QUERY OPERATIONS ====================

    @Override
    @Transactional(readOnly = true)
    public HexMovementResponse getMovementById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Movement ID must be a positive integer");
        }
        
        return movementRepository.findById(id)
                .map(movementMapper::toResponse)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HexMovementResponse> getAllMovements() {
        log.debug("Fetching all movements");
        return movementRepository.findAll().stream()
                .map(movementMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HexMovementResponse> getMovementsByType(MoveType moveType) {
        if (moveType == null) {
            throw new IllegalArgumentException("Movement type cannot be null");
        }
        
        log.debug("Fetching movements by type: {}", moveType);
        return movementRepository.findByMoveType(moveType).stream()
                .map(movementMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HexMovementResponse> getMovementsByDateRange(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start and end dates cannot be null");
        }
        if (startDate.after(endDate)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }
        
        log.debug("Fetching movements between {} and {}", startDate, endDate);
        return movementRepository.findByDateRange(startDate, endDate).stream()
                .map(movementMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HexMovementResponse> getMovementsByUser(Integer userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be a positive integer");
        }
        
        log.debug("Fetching movements by user: {}", userId);
        return movementRepository.findByUserId(userId).stream()
                .map(movementMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HexMovementResponse> getMovementsByMaterial(Integer materialId) {
        if (materialId == null || materialId <= 0) {
            throw new IllegalArgumentException("Material ID must be a positive integer");
        }
        
        log.debug("Fetching movements by material: {}", materialId);
        return movementRepository.findByMaterialId(materialId).stream()
                .map(movementMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HexMovementResponse> getMovementsPaged(Integer pageNumber, Integer pageSize) {
        if (pageNumber == null || pageNumber < 0) {
            throw new IllegalArgumentException("Page number must be non-negative");
        }
        if (pageSize == null || pageSize <= 0) {
            throw new IllegalArgumentException("Page size must be positive");
        }
        
        log.debug("Fetching movements - page: {}, size: {}", pageNumber, pageSize);
        return movementRepository.findPaged(pageNumber, pageSize).stream()
                .map(movementMapper::toResponse)
                .collect(Collectors.toList());
    }

    // ==================== COMMAND OPERATIONS ====================

    @Override
    public HexMovementResponse createMovement(CreateMovementRequest request) {
        validateCreateRequest(request);
        
        log.info("Creating movement - Type: {}, Material: {}, User: {}, Quantity: {}", 
                request.getMoveType(), request.getMaterialId(), request.getUserId(), request.getQuantity());
        
        Movement movement = movementMapper.toDomain(request);
        Movement savedMovement = movementRepository.save(movement);
        
        log.info("Movement created successfully with ID: {}", savedMovement.getId());
        return movementMapper.toResponse(savedMovement);
    }

    @Override
    public HexMovementResponse updateMovement(Integer id, UpdateMovementRequest request) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Movement ID must be a positive integer");
        }
        if (request == null) {
            throw new IllegalArgumentException("Update request cannot be null");
        }
        
        log.info("Updating movement ID: {}", id);
        
        Movement existing = movementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movement not found with ID: " + id));
        
        Movement updated = movementMapper.updateFromRequest(existing, request);
        Movement savedMovement = movementRepository.save(updated);
        
        log.info("Movement ID {} updated successfully", id);
        return movementMapper.toResponse(savedMovement);
    }

    @Override
    public void deleteMovement(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Movement ID must be a positive integer");
        }
        
        if (!movementRepository.existsById(id)) {
            throw new RuntimeException("Movement not found with ID: " + id);
        }
        
        log.info("Deleting movement ID: {}", id);
        movementRepository.deleteById(id);
        log.info("Movement ID {} deleted successfully", id);
    }

    // ==================== VALIDATION HELPERS ====================

    private void validateCreateRequest(CreateMovementRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Create request cannot be null");
        }
        if (request.getMoveType() == null) {
            throw new IllegalArgumentException("Movement type is required");
        }
        if (request.getMaterialId() == null || request.getMaterialId() <= 0) {
            throw new IllegalArgumentException("Material ID must be a positive integer");
        }
        if (request.getUserId() == null || request.getUserId() <= 0) {
            throw new IllegalArgumentException("User ID must be a positive integer");
        }
        if (request.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
    }
}
