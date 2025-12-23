package com.techmate.techmate.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.BooleanSchema;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * Configuración de OpenAPI 3.0 / SpringDoc / Swagger UI.
 *
 * FUNCIONALIDADES:
 * ✅ Documentación automática en: /swagger-ui.html
 * ✅ JSON especificación OpenAPI en: /v3/api-docs
 * ✅ Soporte JWT Bearer Token (OAuth 2.0 compatible)
 * ✅ Nombres en snake_case (sincronizado con Jackson config)
 * ✅ BigDecimal renderizado como number con precisión
 * ✅ Esquemas de error bien documentados
 *
 * IMPORTANTE:
 * Este config fuerza a Swagger a usar el ObjectMapper de Spring (con
 * SNAKE_CASE)
 * en lugar de leer los nombres Java directamente. Esto garantiza que la
 * documentación
 * coincida exactamente con lo que la API devuelve en JSON.
 *
 * @author TechShare Team
 * @version v1.0.0
 */
@Configuration
public class OpenApiConfig {

        /**
         * Configuración estática de SpringDoc.
         * Nota: El snake_case se sincroniza automáticamente desde
         * application.properties
         * via spring.jackson.property-naming-strategy=SNAKE_CASE
         */
        static {
                // SpringDoc lee la configuración de Jackson automáticamente
                // incluyendo PropertyNamingStrategy
        }

        /**
         * Bean principal que define toda la configuración OpenAPI.
         * Incluye:
         * - Información de la API (título, versión, descripción)
         * - Esquema de seguridad JWT Bearer
         * - Definiciones reutilizables de errores
         *
         * @return Configuración OpenAPI completa
         */
        @Bean
        public OpenAPI customOpenAPI() {
                final String securitySchemeName = "bearerAuth";

                // ============================================
                // ESQUEMA: ApiErrorResponse
                // Usado por GlobalExceptionHandler para todas las excepciones
                // ============================================
                Schema<?> apiErrorSchema = new ObjectSchema()
                                .description("Respuesta estándar de error de la API")
                                .addProperty("timestamp",
                                                new StringSchema()
                                                                .description("ISO 8601 timestamp del error")
                                                                .example("2025-12-23T20:45:30Z")
                                                                .format("date-time"))
                                .addProperty("status",
                                                new IntegerSchema()
                                                                .description("HTTP status code")
                                                                .example(400))
                                .addProperty("path",
                                                new StringSchema()
                                                                .description("Ruta de la solicitud que generó el error")
                                                                .example("/api/v1/materials"))
                                .addProperty("code",
                                                new StringSchema()
                                                                .description("Código de error específico de la aplicación")
                                                                .example("MATERIAL_NOT_FOUND"))
                                .addProperty("message",
                                                new StringSchema()
                                                                .description("Mensaje de error legible")
                                                                .example("Material con ID 123 no encontrado"))
                                .addProperty("errors",
                                                new ArraySchema()
                                                                .description("Lista de errores de validación (cuando aplica)")
                                                                .items(new StringSchema()
                                                                                .example("El campo 'nombre' es requerido")));

                // ============================================
                // ESQUEMA: PageResponse
                // Usado para respuestas paginadas
                // ============================================
                Schema<?> pageResponseSchema = new ObjectSchema()
                                .description("Respuesta paginada genérica")
                                .addProperty("content",
                                                new ArraySchema()
                                                                .description("Lista de elementos en esta página"))
                                .addProperty("page_number",
                                                new IntegerSchema()
                                                                .description("Número de página actual (0-indexed)")
                                                                .example(0))
                                .addProperty("page_size",
                                                new IntegerSchema()
                                                                .description("Elementos por página")
                                                                .example(10))
                                .addProperty("total_elements",
                                                new IntegerSchema()
                                                                .description("Total de elementos disponibles")
                                                                .example(42))
                                .addProperty("total_pages",
                                                new IntegerSchema()
                                                                .description("Total de páginas disponibles")
                                                                .example(5))
                                .addProperty("is_first",
                                                new BooleanSchema()
                                                                .description("¿Es la primera página?"))
                                .addProperty("is_last",
                                                new BooleanSchema()
                                                                .description("¿Es la última página?"));

                return new OpenAPI()

                                // ============================================
                                // INFO: Datos básicos de la API
                                // ============================================
                                .info(new Info()
                                                .title("🔧 TechShare API")
                                                .version("1.0.0")
                                                .description("API RESTful para gestión de inventario, materiales, préstamos y usuarios en TechShare.\n\n"
                                                                +
                                                                "## Características\n" +
                                                                "- ✅ Autenticación JWT Bearer\n" +
                                                                "- ✅ Control de acceso basado en roles (RBAC)\n" +
                                                                "- ✅ Gestión completa de materiales y categorías\n" +
                                                                "- ✅ Sistema de préstamos con validaciones de stock\n" +
                                                                "- ✅ Respuestas JSON en snake_case\n" +
                                                                "- ✅ Manejo centralizado de errores\n" +
                                                                "- ✅ Paginación en todas las listas\n\n" +
                                                                "## Cómo usar\n" +
                                                                "1. Obtén un token JWT usando `/auth/login`\n" +
                                                                "2. Haz clic en 'Authorize' y pega: `Bearer <tu_token>`\n"
                                                                +
                                                                "3. Todas tus solicitudes estarán autenticadas automáticamente")
                                                .contact(new Contact()
                                                                .name("TechShare Team")
                                                                .url("https://github.com/DARKTOTEM2703/Back-End-TechShare-Java")
                                                                .email("support@techshare.local"))
                                                .license(new License()
                                                                .name("MIT License")
                                                                .url("https://opensource.org/licenses/MIT")))

                                // ============================================
                                // SECURITY: JWT Bearer Token
                                // ============================================
                                .addSecurityItem(new SecurityRequirement()
                                                .addList(securitySchemeName))

                                // ============================================
                                // COMPONENTS: Esquemas reutilizables
                                // ============================================
                                .components(new Components()
                                                .addSecuritySchemes(securitySchemeName,
                                                                new SecurityScheme()
                                                                                .name(securitySchemeName)
                                                                                .type(SecurityScheme.Type.HTTP)
                                                                                .description("JWT Bearer token. Obtén uno en POST /auth/login")
                                                                                .scheme("bearer")
                                                                                .bearerFormat("JWT"))
                                                .addSchemas("ApiErrorResponse", apiErrorSchema)
                                                .addSchemas("PageResponse", pageResponseSchema));
        }

        /**
         * Agrupa los endpoints públicos (sin autenticación) en la documentación.
         * Esto facilita la navegación en Swagger UI.
         *
         * @return Configuración del grupo de APIs públicas
         */
        @Bean
        public GroupedOpenApi publicApi() {
                return GroupedOpenApi.builder()
                                .group("public")
                                .displayName("🌐 Public Endpoints")
                                .pathsToMatch(
                                                "/auth/**",
                                                "/public/**",
                                                "/materials/public/**")
                                .build();
        }

        /**
         * Agrupa los endpoints protegidos (requieren autenticación) en la
         * documentación.
         *
         * @return Configuración del grupo de APIs protegidas
         */
        @Bean
        public GroupedOpenApi protectedApi() {
                return GroupedOpenApi.builder()
                                .group("protected")
                                .displayName("🔒 Protected Endpoints")
                                .pathsToMatch(
                                                "/admin/**",
                                                "/users/**",
                                                "/materials/**",
                                                "/borrows/**",
                                                "/categories/**",
                                                "/movements/**")
                                .build();
        }
}
