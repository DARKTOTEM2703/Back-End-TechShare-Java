package com.techmate.techmate.hexagonal.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration for API Versioning.
 * 
 * Strategy: URI Path Versioning
 * - Old API: /api/v1/materials
 * - New API: /api/v2/materials
 * 
 * Benefits:
 * - Clear versioning in the URL
 * - Easy to cache and route
 * - Client can choose version explicitly
 * - Allows gradual migration between versions
 */
@Configuration
public class ApiVersionConfig implements WebMvcConfigurer {

    @Override
    public void configurePathMatch(@NonNull PathMatchConfigurer configurer) {
        // Nota: setUseTrailingSlashMatch está deprecado en Spring 6.0+
        // La configuración de trailing slashes se maneja automáticamente
    }
}


