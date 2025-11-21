package com.techmate.techmate.event;

import com.techmate.techmate.entity.Materials;

import lombok.Getter;

/**
 * Event fired when a material's stock falls below a critical threshold.
 * This event can trigger:
 * - Email notifications to admins
 * - Dashboard alerts
 * - Automatic purchase orders
 */
@Getter
public class MaterialLowStockEvent extends DomainEvent {
    
    private final int materialId;
    private final String materialName;
    private final int currentStock;
    private final int threshold;
    
    public MaterialLowStockEvent(Materials material, int threshold) {
        super(material);
        this.materialId = material.getId();
        this.materialName = material.getName();
        this.currentStock = material.getStock();
        this.threshold = threshold;
    }
    
    @Override
    public String getEventType() {
        return "MATERIAL_LOW_STOCK";
    }
    
    @Override
    public String toString() {
        return String.format(
            "MaterialLowStockEvent[materialId=%d, name='%s', currentStock=%d, threshold=%d, eventId=%s]",
            materialId, materialName, currentStock, threshold, getEventId()
        );
    }
}

