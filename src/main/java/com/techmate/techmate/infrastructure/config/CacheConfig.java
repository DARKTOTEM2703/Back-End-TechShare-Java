package com.techmate.techmate.infrastructure.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Configuración de caché para mejorar performance.
 * 
 * ESTRATEGIA DE CACHEO:
 * - Categorías: datos que raramente cambian
 * - SubCategorías: datos que raramente cambian
 * - Roles: datos casi estáticos
 * 
 * MEJORA ESPERADA:
 * - 80-90% reducción en latencia para endpoints frecuentes
 * - Menos carga en base de datos
 * - Mejor experiencia de usuario
 * 
 * USO:
 * - @Cacheable("categories") en métodos de lectura
 * - @CacheEvict("categories", allEntries=true) en métodos de escritura
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "caffeine", matchIfMissing = true)
@EnableCaching
public class CacheConfig {
    
    @Bean
    @ConditionalOnMissingBean(CacheManager.class)
    public CacheManager cacheManager() {
        log.info("Configurando Caffeine Cache Manager");
        
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
            "categories",      // Cache para categorías
            "subcategories",   // Cache para subcategorías
            "roles",           // Cache para roles
            "materials"        // Cache para materiales (listados)
        );
        
        cacheManager.setCaffeine(caffeineConfig());
        
        log.info("Cache configurado: categories, subcategories, roles, materials");
        log.info("Cache settings: maxSize=1000, ttl=30min, stats=enabled");
        
        return cacheManager;
    }
    
    private Caffeine<Object, Object> caffeineConfig() {
        return Caffeine.newBuilder()
            .maximumSize(1000)                           // Máximo 1000 entradas por cache
            .expireAfterWrite(30, TimeUnit.MINUTES)      // Expira después de 30 min
            .recordStats();                              // Habilita estadísticas
    }
}









