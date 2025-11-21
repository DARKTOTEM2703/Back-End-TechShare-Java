package com.techmate.techmate.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración web para CORS, recursos estáticos y otras configuraciones MVC.
 * Utiliza AppProperties para configuración centralizada y tipada.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AppProperties appProperties;

    public WebConfig(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        AppProperties.Cors corsConfig = appProperties.getCors();
        String[] origins = corsConfig.getAllowedOrigins().toArray(new String[0]);
        
        // CORS para autenticación (login, register, verify)
        registry.addMapping("/login")
                .allowedOrigins(origins)
                .allowedMethods("POST", "OPTIONS")
                .allowedHeaders(corsConfig.getAllowedHeaders())
                .allowCredentials(corsConfig.isAllowCredentials())
                .maxAge(corsConfig.getMaxAge());
        
        registry.addMapping("/api/auth/**")
                .allowedOrigins(origins)
                .allowedMethods("POST", "OPTIONS")
                .allowedHeaders(corsConfig.getAllowedHeaders())
                .allowCredentials(corsConfig.isAllowCredentials())
                .maxAge(corsConfig.getMaxAge());
        
        registry.addMapping("/verify")
                .allowedOrigins(origins)
                .allowedMethods("GET", "POST", "OPTIONS")
                .allowedHeaders(corsConfig.getAllowedHeaders())
                .allowCredentials(corsConfig.isAllowCredentials())
                .maxAge(corsConfig.getMaxAge());
        
        // CORS para endpoints de API y admin
        registry.addMapping("/api/**")
                .allowedOrigins(origins)
                .allowedMethods(corsConfig.getAllowedMethods())
                .allowedHeaders(corsConfig.getAllowedHeaders())
                .allowCredentials(corsConfig.isAllowCredentials())
                .maxAge(corsConfig.getMaxAge());
                
        registry.addMapping("/admin/**")
                .allowedOrigins(origins)
                .allowedMethods(corsConfig.getAllowedMethods())
                .allowedHeaders(corsConfig.getAllowedHeaders())
                .allowCredentials(corsConfig.isAllowCredentials())
                .maxAge(corsConfig.getMaxAge());
                
        // CORS para Swagger UI y documentación
        registry.addMapping("/swagger-ui/**")
                .allowedOrigins(origins)
                .allowedMethods("GET")
                .allowedHeaders("*")
                .maxAge(corsConfig.getMaxAge());
                
        registry.addMapping("/v3/api-docs/**")
                .allowedOrigins(origins)
                .allowedMethods("GET")
                .allowedHeaders("*")
                .maxAge(corsConfig.getMaxAge());
    }

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // Servir imágenes estáticas desde el directorio de uploads
        String uploadDir = appProperties.getStorage().getLocation();
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + uploadDir + "/")
                .setCachePeriod(3600); // Cache por 1 hora
    }
}

