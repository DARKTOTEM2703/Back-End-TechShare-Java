package com.techmate.techmate.core.domain.model.movement;

import java.util.Date;
import java.util.Objects;

/**
 * Movement Domain Model - Immutable domain entity for movement operations.
 * Zero Spring/JPA dependencies - pure business logic layer.
 * 
 * Represents the movement/transaction of materials in the system.
 * Tracks BORROW, RETURN, STOCK_ADD, and ADJUSTMENT operations.
 * 
 * @author TechShare Team
 * @version 1.0
 */
public class Movement {

    private final Integer id;
    private final MoveType moveType;
    private final Integer resourceId;
    private final String notes;
    private final Date movementDate;
    private final Integer userId;
    private final Integer materialId;
    
    // Quantity is transient in domain, calculated from operation type
    private final int quantity;

    private Movement(Builder builder) {
        this.id = builder.id;
        this.moveType = builder.moveType;
        this.resourceId = builder.resourceId;
        this.notes = builder.notes;
        this.movementDate = builder.movementDate;
        this.userId = builder.userId;
        this.materialId = builder.materialId;
        this.quantity = builder.quantity;
        
        validateMovement();
    }

    // Getters
    public Integer getId() {
        return id;
    }

    public MoveType getMoveType() {
        return moveType;
    }

    public Integer getResourceId() {
        return resourceId;
    }

    public String getNotes() {
        return notes;
    }

    public Date getMovementDate() {
        return movementDate;
    }

    public Integer getUserId() {
        return userId;
    }

    public Integer getMaterialId() {
        return materialId;
    }

    public int getQuantity() {
        return quantity;
    }

    // Business logic methods
    public boolean isBorrow() {
        return MoveType.BORROW == moveType;
    }

    public boolean isReturn() {
        return MoveType.RETURN == moveType;
    }

    public boolean isStockAdd() {
        return MoveType.STOCK_ADD == moveType;
    }

    public boolean isAdjustment() {
        return MoveType.ADJUSTMENT == moveType;
    }

    /**
     * Calculate impact on stock based on movement type.
     * - BORROW: Decreases stock (negative)
     * - RETURN: Increases stock (positive)
     * - STOCK_ADD: Increases stock (positive)
     * - ADJUSTMENT: Can be positive or negative based on notes
     */
    public int calculateStockImpact() {
        switch (moveType) {
            case BORROW:
                return -quantity;
            case RETURN:
            case STOCK_ADD:
                return quantity;
            case ADJUSTMENT:
                // ADJUSTMENT impact depends on quantity sign
                return quantity;
            default:
                return 0;
        }
    }

    /**
     * Validate movement business rules
     */
    private void validateMovement() {
        if (moveType == null) {
            throw new IllegalArgumentException("Movement type cannot be null");
        }
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be a positive integer");
        }
        if (materialId == null || materialId <= 0) {
            throw new IllegalArgumentException("Material ID must be a positive integer");
        }
        if (movementDate == null) {
            throw new IllegalArgumentException("Movement date cannot be null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movement movement = (Movement) o;
        return Objects.equals(id, movement.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Movement{" +
                "id=" + id +
                ", moveType=" + moveType +
                ", resourceId=" + resourceId +
                ", quantity=" + quantity +
                ", movementDate=" + movementDate +
                ", userId=" + userId +
                ", materialId=" + materialId +
                '}';
    }

    // Builder Pattern
    public static class Builder {
        private Integer id;
        private MoveType moveType;
        private Integer resourceId;
        private String notes;
        private Date movementDate;
        private Integer userId;
        private Integer materialId;
        private int quantity;

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder moveType(MoveType moveType) {
            this.moveType = moveType;
            return this;
        }

        public Builder resourceId(Integer resourceId) {
            this.resourceId = resourceId;
            return this;
        }

        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public Builder movementDate(Date movementDate) {
            this.movementDate = movementDate;
            return this;
        }

        public Builder userId(Integer userId) {
            this.userId = userId;
            return this;
        }

        public Builder materialId(Integer materialId) {
            this.materialId = materialId;
            return this;
        }

        public Builder quantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public Movement build() {
            return new Movement(this);
        }
    }
}
