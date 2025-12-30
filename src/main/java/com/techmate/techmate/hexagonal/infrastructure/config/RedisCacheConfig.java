package com.techmate.techmate.hexagonal.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis Cache Configuration.
 * 
 * Provides distributed caching using Redis instead of Caffeine.
 * This allows multiple instances of the application to share the same cache.
 * 
 * Benefits:
 * - Shared cache across multiple app instances
 * - Persistent cache (survives app restarts)
 * - Horizontal scalability
 * - Cache eviction policies
 * 
 * Enable with: spring.cache.type=redis
 */
@Configuration
@EnableCaching
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis")
public class RedisCacheConfig {
    
    /**
     * Construye un ObjectMapper configurado solo para uso interno en el cache (no lo registra
     * como bean para evitar que Spring Boot lo use como el mapper global de la aplicación).
     */
    private ObjectMapper createCacheObjectMapper() {
    ObjectMapper mapper = com.techmate.techmate.hexagonal.infrastructure.config.JacksonConfig.objectMapper();
        mapper.registerModule(new JavaTimeModule());
        // Solo activar typing para el mapper usado por Redis (necesario para GenericJackson2JsonRedisSerializer
        // si se cachean tipos polimórficos). No exponer este mapper como bean global.
        mapper.activateDefaultTyping(
            mapper.getPolymorphicTypeValidator(),
            ObjectMapper.DefaultTyping.NON_FINAL
        );
        return mapper;
    }
    
    /**
     * RedisCacheManager configurado con TTLs personalizados por cache.
     */
    @Bean
    @ConditionalOnMissingBean(CacheManager.class)
    public RedisCacheManager cacheManager(
        RedisConnectionFactory connectionFactory
    ) {
        // Configuración por defecto
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(30)) // TTL por defecto: 30 minutos
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())
            )
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    new GenericJackson2JsonRedisSerializer(createCacheObjectMapper())
                )
            )
            .disableCachingNullValues(); // No cachear valores null
        
        // Configuraciones específicas por cache
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        
        // Categories y Subcategories: cache largo (raramente cambian)
        cacheConfigurations.put("categories", defaultConfig.entryTtl(Duration.ofHours(2)));
        cacheConfigurations.put("subcategories", defaultConfig.entryTtl(Duration.ofHours(2)));
        
        // Roles: cache muy largo (casi nunca cambian)
        cacheConfigurations.put("roles", defaultConfig.entryTtl(Duration.ofHours(24)));
        
        // Materials: cache medio (cambian frecuentemente por stock)
        cacheConfigurations.put("materials", defaultConfig.entryTtl(Duration.ofMinutes(15)));
        
        // Users: cache corto (datos sensibles)
        cacheConfigurations.put("users", defaultConfig.entryTtl(Duration.ofMinutes(5)));
        
        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(defaultConfig)
            .withInitialCacheConfigurations(cacheConfigurations)
            .transactionAware() // Sincronizar con transacciones de Spring
            .build();
    }
}








