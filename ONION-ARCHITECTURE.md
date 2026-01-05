# 🧅 Onion Architecture - TechShare Backend

## 📐 Estructura de Capas

La arquitectura Onion organiza el código en capas concéntricas donde las dependencias **fluyen hacia el centro** (Domain Core).

```
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                        │
│  (API Controllers, DTOs de Request/Response, Validators)     │
│                                                              │
│  📁 presentation/                                            │
│    ├── api/          → REST Controllers (@RestController)   │
│    └── dto/          → Request/Response DTOs                │
└──────────────────────────────────────────────────────────────┘
                            ↓ depends on
┌─────────────────────────────────────────────────────────────┐
│                   INFRASTRUCTURE LAYER                       │
│  (Adapters, Implementaciones, Configuración, Persistencia)   │
│                                                              │
│  📁 infrastructure/                                          │
│    ├── adapter/      → Implementaciones de ports            │
│    ├── config/       → Configuración Spring                 │
│    ├── security/     → JWT, Auth                            │
│    ├── persistence/  → Repositories JPA (implementación)    │
│    └── external/     → APIs externas, Storage               │
└──────────────────────────────────────────────────────────────┘
                            ↓ depends on
┌─────────────────────────────────────────────────────────────┐
│                   APPLICATION LAYER (Core)                   │
│  (Use Cases, Application Services, Ports, DTOs Internos)     │
│                                                              │
│  📁 core/application/                                        │
│    ├── usecase/      → Casos de uso (lógica aplicación)    │
│    ├── port/                                                 │
│    │   ├── input/    → Interfaces de Use Cases             │
│    │   └── output/   → Interfaces para Infrastructure      │
│    ├── dto/          → DTOs internos                        │
│    ├── mapper/       → Conversión Entity ↔ DTO             │
│    └── exception/    → Excepciones de aplicación           │
└──────────────────────────────────────────────────────────────┘
                            ↓ depends on
┌─────────────────────────────────────────────────────────────┐
│                    DOMAIN LAYER (Core)                       │
│  (Entities, Value Objects, Domain Events, Domain Logic)      │
│                                                              │
│  📁 core/domain/                                             │
│    ├── entity/       → Entidades del dominio               │
│    ├── valueobject/  → Objetos de valor inmutables         │
│    ├── event/        → Eventos del dominio                 │
│    └── repository/   → Interfaces de repositorios (ports)  │
│                                                              │
│  ⚠️ NO DEPENDE DE NADA (Sin Spring, sin JPA aquí)          │
└──────────────────────────────────────────────────────────────┘
```

---

## 🎯 Principios Clave

### 1. **Dependencias hacia adentro**
```
Presentation → Infrastructure → Application → Domain
                                            ↑
                                        (CORE)
```

### 2. **Domain NO conoce a nadie**
- Sin anotaciones Spring (`@Service`, `@Component`)
- Sin anotaciones JPA (`@Entity`, `@Table`) en domain puro
- Solo POJO con lógica de negocio

### 3. **Inversión de Dependencias (DIP)**
```java
// ✅ BIEN: Application define la interfaz, Infrastructure la implementa
// core/application/port/output/UserRepositoryPort.java
public interface UserRepositoryPort {
    User findById(Integer id);
}

// infrastructure/adapter/persistence/UserRepositoryAdapter.java
@Component
public class UserRepositoryAdapter implements UserRepositoryPort {
    private final JpaUserRepository jpaRepo; // JPA aquí
    ...
}
```

---

## 📦 Mapeo de Archivos Actuales → Onion

### **Core Domain** (`core/domain/`)
```
domain/entity/        → core/domain/entity/
domain/model/         → core/domain/entity/ (consolidar)
- Materials.java
- Usuario.java
- Borrow.java
- Categories.java
- etc.

Crear nuevos:
domain/valueobject/   → Email, Money, MaterialStatus, etc.
domain/event/         → MaterialCreatedEvent, BorrowApprovedEvent
```

### **Core Application** (`core/application/`)
```
application/usecase/  → core/application/usecase/
application/port/     → core/application/port/
application/dto/      → core/application/dto/
application/mapper/   → core/application/mapper/
application/exception/→ core/application/exception/
application/service/  → core/application/usecase/ (consolidar)
```

### **Infrastructure** (`infrastructure/`)
```
Mantener:
infrastructure/adapter/
infrastructure/config/
infrastructure/security/
infrastructure/health/
infrastructure/imageStorage/

Mover a adapters:
domain/repository/*.java → infrastructure/adapter/persistence/
```

### **Presentation** (`presentation/`)
```
infrastructure/controller/ → presentation/api/
infrastructure/dto/        → presentation/dto/ (si son de API)
infrastructure/validation/ → presentation/validation/
```

---

## 🔄 Flujo de Ejecución

```
1. HTTP Request
   ↓
2. Presentation/API Controller
   - Valida entrada
   - Convierte Request DTO → Application DTO
   ↓
3. Application Use Case (via Port Interface)
   - Lógica de aplicación
   - Orquesta operaciones
   ↓
4. Domain Entity
   - Lógica de negocio
   - Valida reglas de dominio
   ↓
5. Application Port Output (interface)
   ↓
6. Infrastructure Adapter (implementación)
   - Persistencia JPA
   - Servicios externos
   ↓
7. Response (DTO → Entity → DTO → Response)
```

---

## ✅ Ventajas Implementadas

1. **Testabilidad**: Domain y Application testables sin Spring
2. **Independencia del Framework**: Core no conoce Spring/JPA
3. **Flexibilidad**: Cambiar BD sin tocar Domain/Application
4. **Mantenibilidad**: Responsabilidades claras por capa
5. **SOLID al 100%**: Inversión de dependencias completa

---

## 🚀 Siguiente Paso: Migración Gradual

1. ✅ Crear estructura de directorios
2. 🔄 Mover Domain Entities (sin dependencias)
3. 🔄 Mover Application Use Cases y Ports
4. 🔄 Adaptar Infrastructure
5. 🔄 Mover Controllers a Presentation
6. ✅ Actualizar imports
7. ✅ Validar tests

---

## 📚 Referencias

- **Domain-Driven Design** (Eric Evans)
- **Clean Architecture** (Robert C. Martin)
- **Hexagonal Architecture** (Alistair Cockburn)
- **Ports & Adapters Pattern**
