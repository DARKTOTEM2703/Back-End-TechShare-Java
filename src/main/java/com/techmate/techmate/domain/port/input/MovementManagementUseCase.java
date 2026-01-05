package com.techmate.techmate.domain.port.input;

import java.util.Date;
import java.util.List;

import com.techmate.techmate.core.domain.model.movement.MoveType;

/**
 * MovementManagementUseCase - Input port defining movement management operations.
 * Segregates queries from commands following CQRS pattern.
 * 
 * Responsibilities:
 * - Query operations: retrieve movements with various filters
 * - Command operations: create, update, delete movements
 * 
 * @author TechShare Team
 * @version 1.0
 */
public interface MovementManagementUseCase {

    // ==================== QUERY OPERATIONS ====================

    /**
     * Get movement by ID
     * @param id movement identifier
     * @return movement response or null if not found
     */
    HexMovementResponse getMovementById(Integer id);

    /**
     * Get all movements
     * @return list of all movements
     */
    List<HexMovementResponse> getAllMovements();

    /**
     * Get movements by type
     * @param moveType type of movement
     * @return list of movements of specified type
     */
    List<HexMovementResponse> getMovementsByType(MoveType moveType);

    /**
     * Get movements within date range
     * @param startDate start date (inclusive)
     * @param endDate end date (inclusive)
     * @return list of movements in date range
     */
    List<HexMovementResponse> getMovementsByDateRange(Date startDate, Date endDate);

    /**
     * Get movements by user
     * @param userId user identifier
     * @return list of movements created by user
     */
    List<HexMovementResponse> getMovementsByUser(Integer userId);

    /**
     * Get movements by material
     * @param materialId material identifier
     * @return list of movements for material
     */
    List<HexMovementResponse> getMovementsByMaterial(Integer materialId);

    /**
     * Get paginated movements
     * @param pageNumber page number (starting from 0)
     * @param pageSize page size
     * @return list of movements for the page
     */
    List<HexMovementResponse> getMovementsPaged(Integer pageNumber, Integer pageSize);

    // ==================== COMMAND OPERATIONS ====================

    /**
     * Create new movement
     * @param request creation request with movement details
     * @return created movement response
     */
    HexMovementResponse createMovement(CreateMovementRequest request);

    /**
     * Update existing movement
     * @param id movement identifier
     * @param request update request with new details
     * @return updated movement response
     */
    HexMovementResponse updateMovement(Integer id, UpdateMovementRequest request);

    /**
     * Delete movement
     * @param id movement identifier
     */
    void deleteMovement(Integer id);

    // ==================== NESTED DTOs ====================

    /**
     * Response DTO for movement queries
     */
    class HexMovementResponse {
        public Integer id;
        public MoveType moveType;
        public Integer materialId;
        public Integer userId;
        public String notes;
        public Date movementDate;
        public int quantity;
        public String userName;
        public String materialName;

        public HexMovementResponse() {}

        public HexMovementResponse(Integer id, MoveType moveType, Integer materialId, 
                              Integer userId, String notes, Date movementDate, int quantity) {
            this.id = id;
            this.moveType = moveType;
            this.materialId = materialId;
            this.userId = userId;
            this.notes = notes;
            this.movementDate = movementDate;
            this.quantity = quantity;
        }

        // Getters
        public Integer getId() { return id; }
        public MoveType getMoveType() { return moveType; }
        public Integer getMaterialId() { return materialId; }
        public Integer getUserId() { return userId; }
        public String getNotes() { return notes; }
        public Date getMovementDate() { return movementDate; }
        public int getQuantity() { return quantity; }
        public String getUserName() { return userName; }
        public String getMaterialName() { return materialName; }

        // Setters
        public void setId(Integer id) { this.id = id; }
        public void setMoveType(MoveType moveType) { this.moveType = moveType; }
        public void setMaterialId(Integer materialId) { this.materialId = materialId; }
        public void setUserId(Integer userId) { this.userId = userId; }
        public void setNotes(String notes) { this.notes = notes; }
        public void setMovementDate(Date movementDate) { this.movementDate = movementDate; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public void setUserName(String userName) { this.userName = userName; }
        public void setMaterialName(String materialName) { this.materialName = materialName; }
    }

    /**
     * Request DTO for creating movements
     */
    class CreateMovementRequest {
        public MoveType moveType;
        public Integer materialId;
        public Integer userId;
        public int quantity;
        public String notes;

        public CreateMovementRequest() {}

        public CreateMovementRequest(MoveType moveType, Integer materialId, Integer userId, int quantity, String notes) {
            this.moveType = moveType;
            this.materialId = materialId;
            this.userId = userId;
            this.quantity = quantity;
            this.notes = notes;
        }

        // Getters
        public MoveType getMoveType() { return moveType; }
        public Integer getMaterialId() { return materialId; }
        public Integer getUserId() { return userId; }
        public int getQuantity() { return quantity; }
        public String getNotes() { return notes; }

        // Setters
        public void setMoveType(MoveType moveType) { this.moveType = moveType; }
        public void setMaterialId(Integer materialId) { this.materialId = materialId; }
        public void setUserId(Integer userId) { this.userId = userId; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public void setNotes(String notes) { this.notes = notes; }
    }

    /**
     * Request DTO for updating movements
     */
    class UpdateMovementRequest {
        public MoveType moveType;
        public int quantity;
        public String notes;

        public UpdateMovementRequest() {}

        public UpdateMovementRequest(MoveType moveType, int quantity, String notes) {
            this.moveType = moveType;
            this.quantity = quantity;
            this.notes = notes;
        }

        // Getters
        public MoveType getMoveType() { return moveType; }
        public int getQuantity() { return quantity; }
        public String getNotes() { return notes; }

        // Setters
        public void setMoveType(MoveType moveType) { this.moveType = moveType; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public void setNotes(String notes) { this.notes = notes; }
    }
}
