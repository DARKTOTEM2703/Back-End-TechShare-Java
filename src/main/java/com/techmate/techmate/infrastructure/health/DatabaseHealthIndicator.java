package com.techmate.techmate.infrastructure.health;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Custom Health Indicator para MySQL Database.
 * 
 * Verifica:
 * - Conectividad con la base de datos
 * - Tiempo de respuesta de queries
 * - Estado general del servicio
 * 
 * Expuesto en: GET /actuator/health
 */
@Component("databaseHealth")
@Slf4j
@RequiredArgsConstructor
public class DatabaseHealthIndicator implements HealthIndicator {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Health health() {
        try {
            long startTime = System.currentTimeMillis();

            // Query simple para verificar conectividad
            Long borrowCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM borrow",
                    Long.class);

            long responseTime = System.currentTimeMillis() - startTime;

            // Alerta si query tarda > 500ms
            if (responseTime > 500) {
                log.warn("⚠️ Database responding slowly: {}ms", responseTime);
            }

            return Health.up()
                    .withDetail("database", "mysql")
                    .withDetail("status", "available")
                    .withDetail("totalBorrows", borrowCount != null ? borrowCount : 0)
                    .withDetail("responseTime", responseTime + "ms")
                    .withDetail("performanceStatus",
                            responseTime < 100 ? "excellent" : responseTime < 500 ? "good" : "slow")
                    .build();

        } catch (Exception ex) {
            log.error("Database health check failed", ex);

            return Health.down()
                    .withDetail("database", "mysql")
                    .withDetail("status", "unavailable")
                    .withDetail("error", ex.getMessage())
                    .withException(ex)
                    .build();
        }
    }
}
