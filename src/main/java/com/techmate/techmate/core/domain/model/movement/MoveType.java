package com.techmate.techmate.core.domain.model.movement;

/**
 * Movement Type Enum - Domain value object for movement classification.
 * 
 * Defines the types of operations that can be performed on materials:
 * - BORROW: Material being borrowed by user
 * - RETURN: Material being returned by user
 * - STOCK_ADD: Adding stock to inventory
 * - ADJUSTMENT: Inventory adjustment/correction
 * 
 * @author TechShare Team
 * @version 1.0
 */
public enum MoveType {
    BORROW("Préstamo"),
    RETURN("Devolución"),
    STOCK_ADD("Adición de Stock"),
    ADJUSTMENT("Ajuste de Inventario");

    private final String displayName;

    MoveType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Check if this is an outbound movement (reduces stock)
     */
    public boolean isOutbound() {
        return this == BORROW;
    }

    /**
     * Check if this is an inbound movement (increases stock)
     */
    public boolean isInbound() {
        return this == RETURN || this == STOCK_ADD;
    }
}
