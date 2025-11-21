package com.techmate.techmate.actuator;

import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * Health Indicator personalizado para monitorear el pool de conexiones HikariCP.
 * 
 * MÉTRICAS:
 * - Conexiones activas
 * - Conexiones idle
 * - Threads esperando conexión
 * - Total de conexiones
 * 
 * @author TechShare Team
 * @since 0.5.0
 */
@Component
@RequiredArgsConstructor
public class DatabaseConnectionHealthIndicator implements HealthIndicator {

    private final DataSource dataSource;

    @Override
    public Health health() {
        try {
            if (!(dataSource instanceof HikariDataSource hikariDataSource)) {
                return Health.unknown()
                    .withDetail("message", "DataSource no es HikariCP")
                    .build();
            }
            
            var poolMXBean = hikariDataSource.getHikariPoolMXBean();
            
            int activeConnections = poolMXBean.getActiveConnections();
            int idleConnections = poolMXBean.getIdleConnections();
            int totalConnections = poolMXBean.getTotalConnections();
            int threadsAwaitingConnection = poolMXBean.getThreadsAwaitingConnection();
            
            // Calcular porcentaje de uso
            double usagePercentage = (activeConnections * 100.0) / hikariDataSource.getMaximumPoolSize();
            
            // Determinar estado
            Health.Builder builder;
            String status;
            String message;
            
            if (threadsAwaitingConnection > 5) {
                builder = Health.down();
                status = "CONNECTION_EXHAUSTION";
                message = "Pool de conexiones exhausto: " + threadsAwaitingConnection + " threads esperando";
            } else if (usagePercentage > 80) {
                builder = Health.status("WARNING");
                status = "HIGH_USAGE";
                message = "Uso alto del pool de conexiones: " + String.format("%.2f%%", usagePercentage);
            } else {
                builder = Health.up();
                status = "HEALTHY";
                message = "Pool de conexiones operando normalmente";
            }
            
            return builder
                .withDetail("status", status)
                .withDetail("poolName", hikariDataSource.getPoolName())
                .withDetail("activeConnections", activeConnections)
                .withDetail("idleConnections", idleConnections)
                .withDetail("totalConnections", totalConnections)
                .withDetail("threadsAwaitingConnection", threadsAwaitingConnection)
                .withDetail("maxPoolSize", hikariDataSource.getMaximumPoolSize())
                .withDetail("minIdle", hikariDataSource.getMinimumIdle())
                .withDetail("usagePercentage", String.format("%.2f%%", usagePercentage))
                .withDetail("message", message)
                .build();
                
        } catch (Exception e) {
            return Health.down()
                .withDetail("status", "ERROR")
                .withDetail("error", e.getMessage())
                .withDetail("message", "Error al verificar pool de conexiones")
                .build();
        }
    }
}

