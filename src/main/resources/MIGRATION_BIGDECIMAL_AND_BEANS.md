MIGRACIÓN A BigDecimal y Limpieza de Beans

Resumen breve:

- Se migraron los campos monetarios críticos a `java.math.BigDecimal` (precisión) en entidades, DTOs, mappers y servicios.
- Se centralizó la configuración CORS en `SecurityConfig` y se migró la configuración de Jackson a un `Jackson2ObjectMapperBuilderCustomizer` para evitar colisiones de beans.

Cambios concretos realizados:

- Entidades: `Materials.price`, `DetailsBorrow.unitPrice`, `DetailsBorrow.totalPrice`, `Borrow.amount` → `BigDecimal` con `@Column(precision=10, scale=2)`.
- DTOs/Responses: `MaterialsDTO`, `DetailsBorrowDTO`, `BorrowDTO`, `BorrowResponse`, `MaterialResponse` actualizados a `BigDecimal`.
- Mappers/Servicios: `MaterialsMapper`, `BorrowMapper`, `BorrowUserServiceImp` actualizados para usar `.multiply()`, `.add()` y `.setScale(2, RoundingMode.HALF_UP)`.
- Notificaciones: `EmailService` y `MockEmailService` ahora usan `BigDecimal` en firma para amount/penalty.
- Flyway: `src/main/resources/db/migration/V10__migrate_money_to_decimal.sql` añadido (ALTER TABLE ... DECIMAL(10,2)).

Conflicto de Beans (causa y solución):

- Causa: Existía una definición explícita de `Jackson2ObjectMapperBuilder` y además se definía CORS tanto en `WebConfig` como en `SecurityConfig`. Spring Boot autoconfigura un `Jackson2ObjectMapperBuilder` y la duplicidad de beans / configuración CORS provocaba la necesidad de `SPRING_MAIN_ALLOW_BEAN_DEFINITION_OVERRIDING=true`.
- Solución aplicada:
  - Reemplacé la creación manual del builder por un `Jackson2ObjectMapperBuilderCustomizer` (`JacksonConfig#jsonCustomizer`) para integrarse con la autoconfiguración y evitar colisión de nombres.
  - Eliminé `addCorsMappings` en `WebConfig` y mantuve `corsConfigurationSource()` en `SecurityConfig` como única fuente de configuración CORS.

Estado de la compilación:

- `mvnw compile -DskipTests`: compilación del código `main` ✅ (exitosa).
- `mvnw -DskipTests package`: intento de empaquetado ejecutado, pero la fase `test-compile` falló porque los tests del repositorio todavía asumen tipos `double` y nombres legacy (`getBorrowable_stock`, etc.).

Impacto en tests:

- Aproximadamente 55 errores en `src/test/java` derivados de la migración (incompatibilidad de tipos y métodos con nombres legacy). Esto es esperado: los tests deben migrarse a `BigDecimal` o se pueden añadir adaptadores de compatibilidad.

Recomendaciones:

1. Migrar tests a `BigDecimal` (recomendado) para obtener tipos seguros y consistentes.
2. Si prefieres una solución rápida, añadir métodos de compatibilidad (overloads `setPrice(double)`, `setUnitPrice(double)`, y getters/setters legacy `getBorrowable_stock()`/`setBorrowable_stock(int)`) para que la suite actual compile sin cambios en tests.

Opciones para proceder:

- (A) Migrar automáticamente los tests relevantes a `BigDecimal` y corregir nombres legacy, o
- (B) Añadir adaptadores de compatibilidad en entidades/DTOs para pasar la compilación de pruebas sin tocar `src/test`.

Indica la opción y la aplico.
