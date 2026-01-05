package com.techmate.techmate.infrastructure.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Configuración optimizada del pool de conexiones HikariCP.
 * 
 * OPTIMIZACIONES:
 * - Pool size optimizado según CPUs
 * - Timeouts configurados
 * - Connection leak detection
 * - Métricas habilitadas
 * 
 * @author TechShare Team
 * @since 0.5.0
 */
@Configuration
public class DatabasePerformanceConfig {

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driver-class-name:org.postgresql.Driver}")
    private String driverClassName;

    /**
     * Configuración optimizada de HikariCP.
     * 
     * REGLA: pool-size = ((core_count * 2) + effective_spindle_count)
     * Para servidores modernos: ~10-20 conexiones
     */
    @Bean
    @Primary
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();

        // Configuración básica
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName(driverClassName);

        // ============================================
        // POOL SIZE OPTIMIZATION
        // ============================================

        // Tamaño mínimo del pool (siempre activas)
        config.setMinimumIdle(5);

        // Tamaño máximo del pool
        // Formula: (CPUs * 2) + effective_spindle_count
        // Para 4 CPUs + 1 disco: (4*2)+1 = 9
        config.setMaximumPoolSize(20);

        // ============================================
        // TIMEOUTS
        // ============================================

        // Tiempo máximo de espera para obtener conexión (30s)
        config.setConnectionTimeout(30000);

        // Tiempo máximo de vida de una conexión (30 min)
        config.setMaxLifetime(1800000);

        // Tiempo máximo de inactividad (10 min)
        config.setIdleTimeout(600000);

        // ============================================
        // LEAK DETECTION
        // ============================================

        // Detectar conexiones que no se cierran (2 min)
        config.setLeakDetectionThreshold(120000);

        // ============================================
        // PERFORMANCE TUNING
        // ============================================

        // Test de conexión al obtenerla del pool
        config.setConnectionTestQuery("SELECT 1");

        // Nombre del pool (para métricas)
        config.setPoolName("TechShare-HikariCP");

        // Habilitar auto-commit (mejor performance)
        config.setAutoCommit(true);

        // ============================================
        // DB-SPECIFIC OPTIMIZATIONS
        // Apply MySQL-specific optimizations only when using MySQL driver
        // ============================================
        if (driverClassName != null && driverClassName.toLowerCase().contains("mysql")) {
            // Cachear prepared statements
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            // Use server-side prepared statements
            config.addDataSourceProperty("useServerPrepStmts", "true");

            // Use local session state
            config.addDataSourceProperty("useLocalSessionState", "true");

            // Rewrite batched statements
            config.addDataSourceProperty("rewriteBatchedStatements", "true");

            // Cache result set metadata
            config.addDataSourceProperty("cacheResultSetMetadata", "true");

            // Cache server configuration
            config.addDataSourceProperty("cacheServerConfiguration", "true");

            // Lazy loading of result sets
            config.addDataSourceProperty("elideSetAutoCommits", "true");

            // Maintain time stats
            config.addDataSourceProperty("maintainTimeStats", "false");
        }

        // ============================================
        // METRICS & MONITORING
        // ============================================

        // Registrar métricas (integración con Actuator)
        config.setRegisterMbeans(true);

        return new HikariDataSource(config);
    }

    /**
     * Información del pool para health checks.
     */
    public String getPoolInfo(HikariDataSource dataSource) {
        return String.format(
                "Pool: %s | Active: %d | Idle: %d | Waiting: %d | Total: %d",
                dataSource.getPoolName(),
                dataSource.getHikariPoolMXBean().getActiveConnections(),
                dataSource.getHikariPoolMXBean().getIdleConnections(),
                dataSource.getHikariPoolMXBean().getThreadsAwaitingConnection(),
                dataSource.getHikariPoolMXBean().getTotalConnections());
    }
}
