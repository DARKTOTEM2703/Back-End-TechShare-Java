# Inventario inicial para migración a microservicios

Este documento resume los controladores REST y endpoints principales del monolito para identificar bounded contexts y priorizar la extracción a microservicios.

## Controladores identificados (carpeta `infrastructure/controller`)
- AuthController (`infrastructure/security/AuthController.java`) — login / token
- UserController (`infrastructure/controller/UserController.java`) — CRUD usuarios
- AuthenticatedUserController (`infrastructure/controller/AuthenticatedUserController.java`) — endpoints `/me`, perfil
- TokenController / TokenVeriController — utilidades de token / verificación
- MaterialsController / PublicMaterialsController — catálogo de materiales, búsqueda pública
- BorrowController — gestión de préstamos (cambios de estado)
- MovementsController — movimientos/auditoría
- CategoriesController / SubcategoriesController — catálogos
- RoleController — roles y permisos
- EmailController / EmailPreviewController — envío y previsualización de emails
- MaterialsController, PublicMaterialsController — endpoints: `GET /{id}`, `GET /all`, `GET /sorted-by-price`
- MovementsController — endpoints: `GET /{id}`, `GET /all`, `GET /type/{type}`, `GET /filterByDate`

> Nota: la lista completa y rutas concretas están disponibles en los ficheros del código (buscar `@GetMapping`, `@PostMapping`, etc.).

## Bounded contexts propuestos
1. auth-service
   - Login, JWT, refresh, token verification
   - Dependencias: `TokenUtils`, `JwtTokenGeneratorAdapter`
2. user-service
   - CRUD usuarios, perfiles, roles (usuario-role)
   - Dependencias: `UsuarioRepository`, `UsuarioRoleRepository`
3. catalog-service (materials)
   - Gestión de materiales, categorías, subcategorías
   - Endpoints públicos y privados separados
4. borrow-service
   - Flujo de préstamos: creación (may remain in monolith initially), state transitions, stock (coordina con catalog-service)
   - Dependencias: `BorrowStateProcessor`, `BorrowStockManager`, `MovementsRepository`
5. image-service
   - Adapter a MinIO / almacenamiento de imágenes (actual `MinioImageStorageAdapter`)
   - Beneficio inmediato: desacoplar MinIO y permitir testing/escala independiente
6. notification-service
   - Envío de emails, colas y previsualización (EmailController)

## Prioridad recomendada (fases)
- Fase 0: Containerizar y ejecutar monolito con Docker + MinIO local (ya existe `Dockerfile`) — listo para pruebas.
- Fase 1: Extraer `image-service` (baja complejidad, alto impacto para tests/integración). Crear API: upload, download, delete.
- Fase 2: Extraer `auth-service` y `user-service` (alta prioridad para despliegues independientes y seguridad).
- Fase 3: Extraer `catalog-service` y `borrow-service` (más complejo: transacciones y consistencia de stock).
- Fase 4: Crear infra en Kubernetes + Helm charts, CI/CD, observabilidad.

## Artefactos que generaré a continuación (sugeridos)
- `docker-compose.yml` para local (monolito + minio + mysql) — si deseas.
- `services/image-service/` scaffold (maven starter, controlador mínimo, Dockerfile, README).
- `helm/` charts de ejemplo (chart para `image-service`).

---

¿Confirmas que empiece por extraer `image-service` y generar el scaffold (módulo independiente + Dockerfile + ejemplo de API)? Si prefieres otra prioridad, dime cuál.
