package com.techmate.techmate.hexagonal.infrastructure.actuator;

import com.techmate.techmate.hexagonal.domain.repository.MaterialsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Health Indicator personalizado para monitorear el stock de materiales.
 * 
 * ALERTAS:
 * - DOWN: Si hay más de 10 materiales con stock bajo (<5 unidades)
 * - WARNING: Si hay 5-10 materiales con stock bajo
 * - UP: Stock normal
 * 
 * @author TechShare Team
 * @since 0.5.0
 */
@Component
@RequiredArgsConstructor
public class MaterialStockHealthIndicator implements HealthIndicator {

    private final MaterialsRepository materialsRepository;
    
    private static final int LOW_STOCK_THRESHOLD = 5;
    private static final int CRITICAL_COUNT = 10;
    private static final int WARNING_COUNT = 5;

    @Override
    public Health health() {
        try {
            // Buscar materiales con stock bajo
            var lowStockMaterials = materialsRepository.findLowStock(LOW_STOCK_THRESHOLD);
            int lowStockCount = lowStockMaterials.size();
            
            // Calcular total de materiales
            long totalMaterials = materialsRepository.count();
            
            // Determinar estado
            if (lowStockCount >= CRITICAL_COUNT) {
                return Health.down()
                    .withDetail("status", "CRITICAL")
                    .withDetail("lowStockCount", lowStockCount)
                    .withDetail("totalMaterials", totalMaterials)
                    .withDetail("threshold", LOW_STOCK_THRESHOLD)
                    .withDetail("message", "Stock crítico: " + lowStockCount + " materiales con menos de " + LOW_STOCK_THRESHOLD + " unidades")
                    .withDetail("action", "Revisar urgentemente inventario de materiales")
                    .build();
            } else if (lowStockCount >= WARNING_COUNT) {
                return Health.status("WARNING")
                    .withDetail("status", "WARNING")
                    .withDetail("lowStockCount", lowStockCount)
                    .withDetail("totalMaterials", totalMaterials)
                    .withDetail("threshold", LOW_STOCK_THRESHOLD)
                    .withDetail("message", "Advertencia de stock: " + lowStockCount + " materiales con menos de " + LOW_STOCK_THRESHOLD + " unidades")
                    .withDetail("action", "Planificar reabastecimiento")
                    .build();
            } else {
                return Health.up()
                    .withDetail("status", "HEALTHY")
                    .withDetail("lowStockCount", lowStockCount)
                    .withDetail("totalMaterials", totalMaterials)
                    .withDetail("threshold", LOW_STOCK_THRESHOLD)
                    .withDetail("message", "Stock en niveles normales")
                    .build();
            }
            
        } catch (Exception e) {
            return Health.down()
                .withDetail("status", "ERROR")
                .withDetail("error", e.getMessage())
                .withDetail("message", "Error al verificar stock de materiales")
                .build();
        }
    }
}








