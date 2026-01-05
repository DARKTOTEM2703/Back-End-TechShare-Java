package com.techmate.techmate.infrastructure.actuator;

import com.techmate.techmate.infrastructure.persistence.entity.Status;
import com.techmate.techmate.infrastructure.persistence.repository.BorrowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Health Indicator personalizado para monitorear el sistema de préstamos.
 * 
 * ALERTAS:
 * - DOWN: Si hay más de 50 préstamos activos (capacidad excedida)
 * - WARNING: Si hay 30-50 préstamos activos
 * - UP: Capacidad normal
 * 
 * @author TechShare Team
 * @since 0.5.0
 */
@Component
@RequiredArgsConstructor
public class BorrowSystemHealthIndicator implements HealthIndicator {

    private final BorrowRepository borrowRepository;
    
    private static final int MAX_CAPACITY = 50;
    private static final int WARNING_CAPACITY = 30;

    @Override
    public Health health() {
        try {
            // Contar préstamos activos (BORROWED = préstamos activos en uso)
            long activeBorrows = borrowRepository.countByStatus(Status.BORROWED);
            
            // Contar préstamos pendientes (PROCESS = en proceso de aprobación)
            long pendingBorrows = borrowRepository.countByStatus(Status.PENDING);
            
            // Total de préstamos en el sistema
            long totalBorrows = borrowRepository.count();
            
            // Calcular porcentaje de uso
            double usagePercentage = (activeBorrows * 100.0) / MAX_CAPACITY;
            
            // Determinar estado
            if (activeBorrows >= MAX_CAPACITY) {
                return Health.down()
                    .withDetail("status", "OVERLOAD")
                    .withDetail("activeBorrows", activeBorrows)
                    .withDetail("pendingBorrows", pendingBorrows)
                    .withDetail("totalBorrows", totalBorrows)
                    .withDetail("maxCapacity", MAX_CAPACITY)
                    .withDetail("usagePercentage", String.format("%.2f%%", usagePercentage))
                    .withDetail("message", "Sistema de préstamos al límite: " + activeBorrows + " préstamos activos")
                    .withDetail("action", "No aceptar nuevos préstamos hasta liberar capacidad")
                    .build();
            } else if (activeBorrows >= WARNING_CAPACITY) {
                return Health.status("WARNING")
                    .withDetail("status", "HIGH_LOAD")
                    .withDetail("activeBorrows", activeBorrows)
                    .withDetail("pendingBorrows", pendingBorrows)
                    .withDetail("totalBorrows", totalBorrows)
                    .withDetail("maxCapacity", MAX_CAPACITY)
                    .withDetail("usagePercentage", String.format("%.2f%%", usagePercentage))
                    .withDetail("message", "Carga alta en sistema de préstamos: " + activeBorrows + " préstamos activos")
                    .withDetail("action", "Monitorear capacidad de préstamos")
                    .build();
            } else {
                return Health.up()
                    .withDetail("status", "HEALTHY")
                    .withDetail("activeBorrows", activeBorrows)
                    .withDetail("pendingBorrows", pendingBorrows)
                    .withDetail("totalBorrows", totalBorrows)
                    .withDetail("maxCapacity", MAX_CAPACITY)
                    .withDetail("usagePercentage", String.format("%.2f%%", usagePercentage))
                    .withDetail("message", "Sistema de préstamos operando normalmente")
                    .build();
            }
            
        } catch (Exception e) {
            return Health.down()
                .withDetail("status", "ERROR")
                .withDetail("error", e.getMessage())
                .withDetail("message", "Error al verificar sistema de préstamos")
                .build();
        }
    }
}









