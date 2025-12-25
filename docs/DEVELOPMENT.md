# 🛠️ Guía de Desarrollo - TechShare Backend

Documentación para desarrolladores que trabajan en el proyecto.

---

## 📋 Tabla de Contenidos

1. [Configuración del Entorno](#configuración-del-entorno)
2. [Estructura del Proyecto](#estructura-del-proyecto)
3. [Estándares de Código](#estándares-de-código)
4. [Git Workflow](#git-workflow)
5. [Testing](#testing)
6. [Debugging](#debugging)
7. [Agregar Nuevas Features](#agregar-nuevas-features)
8. [Contribuir](#contribuir)

---

## 🖥️ Configuración del Entorno

### Requisitos
- **JDK 17+** (recomendado: JDK 23 para testing)
- **Maven 3.8+**
- **MySQL 8.0+** (local o Docker)
- **Git**
- **IDE**: IntelliJ IDEA (recomendado) o VS Code con extensiones Java

### Setup Inicial

```bash
# Clonar el repositorio
git clone <repository-url>
cd Back-End-TechShare-Java

# Configurar variables de entorno
cp .env.example .env

# Iniciar base de datos con Docker
docker-compose up -d mysql

# Ejecutar migraciones
mvn flyway:migrate

# Ejecutar la aplicación
mvn spring-boot:run
```

### Variables de Entorno (Desarrollo)

```properties
# Base de datos
DB_HOST=localhost
DB_PORT=3306
DB_NAME=techmate
DB_USER=root
DB_PASSWORD=root

# JWT
JWT_SECRET=your-dev-secret-key-change-in-production
JWT_EXPIRATION=86400000

# Swagger
SWAGGER_ENABLED=true

# Logging
LOGGING_LEVEL_ROOT=INFO
LOGGING_LEVEL_HIBERNATE=DEBUG
```

---

## 📁 Estructura del Proyecto

```
src/main/java/com/techmate/techmate/
├── Controller/          # REST Controllers
│   ├── MaterialsController.java
│   ├── BorrowController.java
│   └── ...
├── DTO/                 # Data Transfer Objects
│   ├── materials/
│   ├── borrow/
│   └── ...
├── Exception/           # Custom Exceptions
│   ├── GlobalExceptionHandler.java
│   └── ApiErrorResponse.java
├── Mapper/              # DTOs ↔ Entities
│   ├── MaterialsMapper.java
│   └── ...
├── Model/               # JPA Entities
│   ├── Materials.java
│   ├── Borrow.java
│   └── ...
├── Repository/          # Spring Data JPA
│   ├── MaterialsRepository.java
│   └── ...
├── Service/             # Business Logic
│   ├── MaterialsService.java
│   └── ...
├── security/            # JWT & Security Config
│   ├── WebSecurityConfig.java
│   ├── JwtTokenProvider.java
│   └── ...
└── config/              # Spring Configuration
    └── CacheConfig.java

src/main/resources/
├── application.properties    # Config principal
└── db/migration/            # Flyway migrations
    ├── V1__init.sql
    ├── V2__add_id_to_usuario_role.sql
    ├── V3__seed_e2e_user.sql
    └── V4__add_performance_indexes.sql

src/test/java/
├── controller/          # Controller tests
├── service/             # Service tests
└── repository/          # Repository tests
```

---

## 📝 Estándares de Código

### Java Code Style

#### Naming Conventions
```java
// Classes: PascalCase
public class MaterialsController { }

// Methods/Variables: camelCase
public void getMaterialById() { }
private String materialName;

// Constants: UPPER_SNAKE_CASE
private static final String DEFAULT_PAGE_SIZE = "10";

// DTOs: Descriptive names with DTO suffix
public class MaterialResponseDTO { }
```

#### Service Layer Pattern
```java
@Service
public class MaterialsService {
    
    private final MaterialsRepository repository;
    
    // Constructor injection (preferido sobre @Autowired)
    public MaterialsService(MaterialsRepository repository) {
        this.repository = repository;
    }
    
    // Business logic aquí
    public MaterialResponseDTO getMaterialById(Long id) {
        Materials material = repository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Material no encontrado"));
        return MaterialsMapper.toResponseDTO(material);
    }
}
```

#### Controller Pattern
```java
@RestController
@RequestMapping("/api/materials")
public class MaterialsController {
    
    private final MaterialsService service;
    
    public MaterialsController(MaterialsService service) {
        this.service = service;
    }
    
    // NO usar try-catch aquí (delegar a GlobalExceptionHandler)
    @GetMapping("/{id}")
    public ResponseEntity<MaterialResponseDTO> getMaterial(@PathVariable Long id) {
        return ResponseEntity.ok(service.getMaterialById(id));
    }
}
```

### Exception Handling

**❌ EVITAR:**
```java
@GetMapping("/{id}")
public ResponseEntity<?> getMaterial(@PathVariable Long id) {
    try {
        MaterialResponseDTO material = service.getMaterialById(id);
        return ResponseEntity.ok(material);
    } catch (EntityNotFoundException e) {
        return ResponseEntity.notFound().build();
    } catch (Exception e) {
        return ResponseEntity.status(500).body("Error");
    }
}
```

**✅ CORRECTO:**
```java
@GetMapping("/{id}")
public ResponseEntity<MaterialResponseDTO> getMaterial(@PathVariable Long id) {
    // GlobalExceptionHandler se encarga de las excepciones
    return ResponseEntity.ok(service.getMaterialById(id));
}
```

### DTOs y Mappers

```java
// 1. Crear DTO
public class MaterialResponseDTO {
    private Long id;
    private String name;
    private String description;
    private Integer quantity;
    // getters, setters, constructors
}

// 2. Crear Mapper
public class MaterialsMapper {
    public static MaterialResponseDTO toResponseDTO(Materials entity) {
        MaterialResponseDTO dto = new MaterialResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        // ...
        return dto;
    }
}

// 3. Usar en Service
public MaterialResponseDTO getMaterialById(Long id) {
    Materials material = repository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Material not found"));
    return MaterialsMapper.toResponseDTO(material);
}
```

---

## 🌿 Git Workflow

### Branch Strategy

```
main (producción)
  ├── develop (desarrollo)
      ├── feature/add-new-endpoint
      ├── fix/fix-borrow-validation
      └── refactor/optimize-queries
```

### Crear una Feature

```bash
# Desde develop
git checkout develop
git pull origin develop

# Crear branch de feature
git checkout -b feature/add-inventory-report

# Hacer commits
git add .
git commit -m "feat: agregar endpoint de reporte de inventario"

# Push y crear PR
git push origin feature/add-inventory-report
```

### Commit Messages

Seguir **Conventional Commits**:

```
feat: agregar endpoint de reportes de inventario
fix: corregir validación de cantidad en préstamos
refactor: optimizar query de materiales con JOIN FETCH
docs: actualizar README con instrucciones de Docker
test: agregar tests para MovementsController
perf: agregar índices a tabla materials
chore: actualizar dependencias de Spring Boot
```

---

## 🧪 Testing

### Ejecutar Tests

```bash
# Todos los tests
mvn test

# Solo tests de un paquete
mvn test -Dtest=MaterialsControllerTest

# Con cobertura
mvn clean test jacoco:report
```

### Escribir Tests

#### Unit Test (Service)
```java
@ExtendWith(MockitoExtension.class)
class MaterialsServiceTest {
    
    @Mock
    private MaterialsRepository repository;
    
    @InjectMocks
    private MaterialsService service;
    
    @Test
    void getMaterialById_ShouldReturnMaterial_WhenExists() {
        // Given
        Materials material = new Materials();
        material.setId(1L);
        material.setName("Arduino UNO");
        
        when(repository.findById(1L)).thenReturn(Optional.of(material));
        
        // When
        MaterialResponseDTO result = service.getMaterialById(1L);
        
        // Then
        assertNotNull(result);
        assertEquals("Arduino UNO", result.getName());
        verify(repository).findById(1L);
    }
    
    @Test
    void getMaterialById_ShouldThrowException_WhenNotFound() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());
        
        // When/Then
        assertThrows(EntityNotFoundException.class, 
            () -> service.getMaterialById(999L));
    }
}
```

#### Integration Test (Controller)
```java
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MaterialsControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void getMaterial_ShouldReturn200_WhenExists() throws Exception {
        mockMvc.perform(get("/api/materials/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Arduino UNO"));
    }
}
```

### Coverage Goals
- **Mínimo:** 70% líneas
- **Target:** 80%+ líneas
- **Crítico:** 100% service layer

---

## 🐛 Debugging

### Logs en Desarrollo

```properties
# application.properties
logging.level.root=INFO
logging.level.com.techmate.techmate=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

### Debugging JPA Queries

```properties
# Ver queries ejecutadas
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Detectar N+1 queries
spring.jpa.properties.hibernate.generate_statistics=true
```

### Debugging con IntelliJ

1. Establecer breakpoints en código
2. Run → Debug 'TechmateApplication'
3. Usar Evaluate Expression (Alt+F8) para inspeccionar variables

### Problemas Comunes

#### Error: "No property 'X' found in entity"
```java
// ❌ INCORRECTO
@Query("SELECT m FROM Materials m WHERE m.material_name = :name")

// ✅ CORRECTO (usar nombre del campo Java)
@Query("SELECT m FROM Materials m WHERE m.name = :name")
```

#### Error: LazyInitializationException
```java
// ❌ PROBLEMA
@GetMapping("/{id}")
public Borrow getBorrow(@PathVariable Long id) {
    Borrow borrow = repository.findById(id).orElseThrow();
    // borrow.getMaterials() falla (lazy)
    return borrow;
}

// ✅ SOLUCIÓN: JOIN FETCH - Sobrescribir `findById` para devolver la entidad con relaciones cargadas
@Query("SELECT b FROM Borrow b " +
    "LEFT JOIN FETCH b.details d " +
    "LEFT JOIN FETCH d.materials m " +
    "LEFT JOIN FETCH m.subCategory " +
    "LEFT JOIN FETCH b.usuario " +
    "WHERE b.id = :id")
Optional<Borrow> findById(@Param("id") Integer id);
```

---

## ➕ Agregar Nuevas Features

### Ejemplo: Agregar endpoint de "Estadísticas"

#### 1. Crear DTO
```java
// DTO/statistics/StatisticsResponseDTO.java
public class StatisticsResponseDTO {
    private Long totalMaterials;
    private Long totalBorrows;
    private Long activeBorrows;
    // getters, setters
}
```

#### 2. Extender Repository (si necesario)
```java
// Repository/MaterialsRepository.java
@Repository
public interface MaterialsRepository extends JpaRepository<Materials, Long> {
    @Query("SELECT COUNT(m) FROM Materials m")
    Long countAllMaterials();
}
```

#### 3. Implementar Service
```java
// Service/StatisticsService.java
@Service
public class StatisticsService {
    private final MaterialsRepository materialsRepository;
    private final BorrowRepository borrowRepository;
    
    // constructor injection
    
    public StatisticsResponseDTO getStatistics() {
        StatisticsResponseDTO dto = new StatisticsResponseDTO();
        dto.setTotalMaterials(materialsRepository.countAllMaterials());
        dto.setTotalBorrows(borrowRepository.count());
        dto.setActiveBorrows(borrowRepository.countByStatus("ACTIVE"));
        return dto;
    }
}
```

#### 4. Crear Controller
```java
// Controller/StatisticsController.java
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {
    private final StatisticsService service;
    
    public StatisticsController(StatisticsService service) {
        this.service = service;
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StatisticsResponseDTO> getStatistics() {
        return ResponseEntity.ok(service.getStatistics());
    }
}
```

#### 5. Agregar Tests
```java
// Test
@Test
void getStatistics_ShouldReturnCorrectCounts() {
    when(materialsRepository.countAllMaterials()).thenReturn(100L);
    when(borrowRepository.count()).thenReturn(50L);
    when(borrowRepository.countByStatus("ACTIVE")).thenReturn(20L);
    
    StatisticsResponseDTO result = service.getStatistics();
    
    assertEquals(100L, result.getTotalMaterials());
    assertEquals(50L, result.getTotalBorrows());
    assertEquals(20L, result.getActiveBorrows());
}
```

#### 6. Documentar en Swagger
```java
@Operation(summary = "Obtener estadísticas del sistema")
@ApiResponse(responseCode = "200", description = "Estadísticas obtenidas exitosamente")
@GetMapping
public ResponseEntity<StatisticsResponseDTO> getStatistics() {
    return ResponseEntity.ok(service.getStatistics());
}
```

---

## 🤝 Contribuir

### Code Review Checklist

Antes de crear un PR, verificar:

- [ ] Código sigue estándares del proyecto
- [ ] Tests agregados/actualizados (coverage >70%)
- [ ] Todos los tests pasan (`mvn test`)
- [ ] Sin warnings de compilación
- [ ] Documentación actualizada (README, Swagger)
- [ ] Commit messages siguen Conventional Commits
- [ ] Branch actualizado con `develop`

### Pull Request Template

```markdown
## Descripción
Descripción breve del cambio.

## Tipo de cambio
- [ ] Bug fix
- [ ] Nueva feature
- [ ] Breaking change
- [ ] Refactor
- [ ] Documentación

## Checklist
- [ ] Tests agregados
- [ ] Documentación actualizada
- [ ] Code review propio realizado
- [ ] Build exitoso

## Screenshots (si aplica)
```

---

## 🔧 Tools & Plugins Recomendados

### IntelliJ IDEA
- Lombok Plugin
- SonarLint
- Rainbow Brackets
- Git ToolBox

### VS Code
- Extension Pack for Java
- Spring Boot Extension Pack
- REST Client
- GitLens

### CLI Tools
```bash
# HTTPie (testing APIs)
http GET localhost:8080/api/materials

# jq (formatear JSON)
curl localhost:8080/api/materials | jq

# Maven Wrapper (no necesita Maven instalado)
./mvnw clean install
```

---

## 📚 Recursos Adicionales

- [Spring Boot Docs](https://spring.io/projects/spring-boot)
- [Spring Data JPA Docs](https://spring.io/projects/spring-data-jpa)
- [Hibernate Best Practices](https://vladmihalcea.com/tutorials/hibernate/)
- [Baeldung Spring Tutorials](https://www.baeldung.com/spring-boot)

---

**Última actualización:** 8 de Octubre 2025
