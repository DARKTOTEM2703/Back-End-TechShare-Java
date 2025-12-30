package com.techmate.techmate.infrastructure.config;

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
        public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
                // Servir imágenes estáticas desde el directorio de uploads
                String uploadDir = appProperties.getStorage().getLocation();
                registry.addResourceHandler("/images/**")
                                .addResourceLocations("file:" + uploadDir + "/")
                                .setCachePeriod(3600); // Cache por 1 hora
        }
}







