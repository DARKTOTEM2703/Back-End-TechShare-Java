package com.techmate.techmate.infrastructure.config;


import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Propiedades de configuración personalizadas de la aplicación.
 * Centraliza todas las propiedades custom para facilitar el mantenimiento.
 * 
 * @see application.properties
 */
@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    /**
     * Configuración de CORS
     */
    @NotNull
    private Cors cors = new Cors();

    /**
     * Configuración de verificación de email
     */
    @NotNull
    private Verification verification = new Verification();

    /**
     * Configuración de almacenamiento de imágenes
     */
    @NotNull
    private Storage storage = new Storage();

    /**
     * URL base del servidor (usado para construir URLs de imágenes)
     */
    @NotBlank
    private String serverUrl = "http://localhost:8080";

    // Getters y Setters (Lombok no está generándolos correctamente)
    public Cors getCors() {
        return cors;
    }

    public void setCors(Cors cors) {
        this.cors = cors;
    }

    public Verification getVerification() {
        return verification;
    }

    public void setVerification(Verification verification) {
        this.verification = verification;
    }

    public Storage getStorage() {
        return storage;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    @Data
    public static class Cors {
        /**
         * Lista de orígenes permitidos para CORS.
         * Ejemplo: http://localhost:3000,http://localhost:3001
         */
        private List<String> allowedOrigins = List.of("http://localhost:3000", "http://localhost:3001");
        
        /**
         * Métodos HTTP permitidos
         */
        private String[] allowedMethods = {"GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"};
        
        /**
         * Headers permitidos
         */
        private String[] allowedHeaders = {"*"};
        
        /**
         * Permitir credenciales (cookies, authorization headers)
         */
        private boolean allowCredentials = true;
        
        /**
         * Tiempo de cache para preflight requests (en segundos)
         */
        private long maxAge = 3600L;

        // Getters y Setters
        public List<String> getAllowedOrigins() {
            return allowedOrigins;
        }

        public void setAllowedOrigins(List<String> allowedOrigins) {
            this.allowedOrigins = allowedOrigins;
        }

        public String[] getAllowedMethods() {
            return allowedMethods;
        }

        public void setAllowedMethods(String[] allowedMethods) {
            this.allowedMethods = allowedMethods;
        }

        public String[] getAllowedHeaders() {
            return allowedHeaders;
        }

        public void setAllowedHeaders(String[] allowedHeaders) {
            this.allowedHeaders = allowedHeaders;
        }

        public boolean isAllowCredentials() {
            return allowCredentials;
        }

        public void setAllowCredentials(boolean allowCredentials) {
            this.allowCredentials = allowCredentials;
        }

        public long getMaxAge() {
            return maxAge;
        }

        public void setMaxAge(long maxAge) {
            this.maxAge = maxAge;
        }
    }

    @Data
    public static class Verification {
        /**
         * URL base para la verificación de email.
         * Se concatena con el token para formar la URL completa.
         */
        @NotBlank
        private String url = "http://localhost:3000/verify?token=";

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    @Data
    public static class Storage {
        /**
         * Directorio donde se almacenan las imágenes subidas.
         * Ruta relativa al directorio de ejecución de la aplicación.
         */
        @NotBlank
        private String location = "uploaded-images";
        
        /**
         * Tamaño máximo de archivo en bytes (10MB por defecto)
         */
        private Long maxFileSize = 10485760L;
        
        /**
         * Tipos de archivo permitidos
         */
        private String[] allowedTypes = {"image/jpeg", "image/png", "image/gif", "image/webp"};

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public Long getMaxFileSize() {
            return maxFileSize;
        }

        public void setMaxFileSize(Long maxFileSize) {
            this.maxFileSize = maxFileSize;
        }

        public String[] getAllowedTypes() {
            return allowedTypes;
        }

        public void setAllowedTypes(String[] allowedTypes) {
            this.allowedTypes = allowedTypes;
        }
    }
}










