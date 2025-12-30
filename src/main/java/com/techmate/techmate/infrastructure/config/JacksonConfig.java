package com.techmate.techmate.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Utility to provide a consistently-configured ObjectMapper for manual uses
 * (places that currently call new ObjectMapper()).
 */
@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> {
            // Use default naming strategy (CAMEL_CASE) for compatibility with Next.js
            // frontend
            builder.modules(new JavaTimeModule());
            builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        };
    }

    // Utility for places that still create ObjectMapper manually.
    public static ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // Use default naming strategy (CAMEL_CASE) for compatibility with Next.js
        // frontend
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}







