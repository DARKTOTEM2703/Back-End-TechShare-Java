package com.techmate.techmate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * Configuración de OpenAPI / Swagger UI.
 *
 * Expone automáticamente la UI en: /swagger-ui.html o /swagger-ui/index.html
 *
 * Seguridad: añadimos un scheme de tipo http Bearer para que puedas probar
 * las rutas protegidas directamente desde la UI (hacer click en Authorize y
 * pegar "Bearer <token>").
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        // Schema para ApiErrorResponse (referenciado en GlobalExceptionHandler)
        Schema<?> apiErrorSchema = new ObjectSchema()
                .addProperty("timestamp", new StringSchema().example("2025-10-09T12:34:56Z"))
                .addProperty("status", new IntegerSchema().example(400))
                .addProperty("path", new StringSchema().example("/api/materials"))
                .addProperty("code", new StringSchema().example("MATERIAL_NOT_FOUND"))
                .addProperty("errors", new ArraySchema().items(new StringSchema().example("Mensaje de error")));

        return new OpenAPI()
                .info(new Info()
                        .title("TechShare API")
                        .version("v0.2.0")
                        .description("API para gestión de inventario y préstamos - TechShare"))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT"))
                        .addSchemas("ApiErrorResponse", apiErrorSchema));
    }

    // OpenApiCustomiser bean removed: schema registered inside customOpenAPI to avoid runtime dependency issues
}


