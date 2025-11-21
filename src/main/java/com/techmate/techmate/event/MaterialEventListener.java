package com.techmate.techmate.event;

import com.techmate.techmate.service.audit.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Event Listener for Material-related domain events.
 * All event handlers are async to avoid blocking the main request thread.
 * 
 * Desacoplamiento de responsabilidades:
 * - MaterialService: Lógica de materiales
 * - MaterialEventListener: Notificaciones, auditoría, efectos secundarios
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MaterialEventListener {
    
    private final AuditService auditService;
    
    /**
     * Handles MaterialLowStockEvent asynchronously.
     * Actions:
     * - Registra la alerta en auditoría
     * - Registra en logs con warning
     * - Podría enviar emails a admins o crear órdenes de compra
     */
    @Async
    @EventListener
    public void handleLowStock(MaterialLowStockEvent event) {
        log.warn(
            "🚨 LOW STOCK ALERT - Material '{}' (ID: {}) has only {} units (threshold: {}). EventID: {}",
            event.getMaterialName(),
            event.getMaterialId(),
            event.getCurrentStock(),
            event.getThreshold(),
            event.getEventId()
        );
        
        try {
            // 📋 Registrar alerta en auditoría
            auditService.logMaterial(
                event.getMaterialId(),
                "LOW_STOCK_ALERT",
                "Stock bajo detectado. Stock actual: " + event.getCurrentStock() + 
                ", Umbral: " + event.getThreshold()
            );
            
            // 📧 Enviar alertas (comentadas - requieren EmailService)
            // emailService.sendLowStockAlert(
            //     adminEmail,
            //     event.getMaterialName(),
            //     event.getCurrentStock(),
            //     event.getThreshold()
            // );
            
            // 🛒 Crear orden de compra automática (comentada - requiere PurchaseOrderService)
            // purchaseOrderService.createAutomaticOrder(
            //     event.getMaterialId(),
            //     calculateQuantityToOrder(event.getCurrentStock())
            // );
        } catch (Exception e) {
            log.error("Error procesando MaterialLowStockEvent para material {}: {}", 
                event.getMaterialId(), e.getMessage(), e);
        }
    }
}


