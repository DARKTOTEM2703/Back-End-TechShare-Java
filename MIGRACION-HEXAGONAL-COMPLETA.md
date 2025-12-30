# 🏗️ MIGRACIÓN COMPLETA A ARQUITECTURA HEXAGONAL

## 📊 Resumen Ejecutivo

**Objetivo**: Migrar todo el backend TechShare a arquitectura hexagonal (Onion Architecture)  
**Estado Actual**: Hexagonal parcial (solo módulo Borrow)  
**Estado Objetivo**: 100% hexagonal en todos los módulos  
**Fecha Inicio**: 28 Diciembre 2025

---

## 🎯 Principios de Arquitectura Hexagonal

### Capas (de adentro hacia afuera)

```
┌─────────────────────────────────────────────────┐
│                                                 │
│  1. DOMAIN (Core - Pure Java)                  │
│     - Entities / Value Objects                  │
│     - Domain Services                           │
│     - Business Rules                            │
│                                                 │
├─────────────────────────────────────────────────┤
│                                                 │
│  2. APPLICATION (Use Cases - Framework agnostic)│
│     - Use Cases / Interactors                   │
│     - Ports (Interfaces)                        │
│       - Input Ports (from controllers)          │
│       - Output Ports (to repositories)          │
│                                                 │
├─────────────────────────────────────────────────┤
│                                                 │
│  3. INFRASTRUCTURE (Adapters - Framework deps)  │
│     - Input Adapters:                           │
│       - REST Controllers                        │
│       - GraphQL Resolvers                       │
│     - Output Adapters:                          │
│       - JPA Repositories                        │
│       - External APIs                           │
│       - Email/Storage Services                  │
│                                                 │
└─────────────────────────────────────────────────┘
```

### Reglas de Dependencia

1. ✅ **Domain** NO depende de nadie
2. ✅ **Application** depende solo de Domain
3. ✅ **Infrastructure** depende de Application y Domain
4. ❌ **Domain** NUNCA importa Spring/JPA/Framework

---

## 📂 Estructura de Directorios Objetivo

```
src/main/java/com/techmate/techmate/
├── domain/                              # CAPA 1: DOMINIO PURO
│   ├── model/                          # Entities & Value Objects
│   │   ├── usuario/
│   │   │   ├── Usuario.java           # Domain Entity (NO JPA)
│   │   │   ├── UserId.java            # Value Object
│   │   │   ├── Email.java             # Value Object
│   │   │   └── Password.java          # Value Object
│   │   ├── borrow/
│   │   │   ├── Borrow.java           ✅ YA EXISTE
│   │   │   ├── DetailsBorrow.java    ✅ YA EXISTE
│   │   │   └── Material.java         ✅ YA EXISTE
│   │   ├── material/
│   │   │   ├── Material.java
│   │   │   ├── Category.java
│   │   │   └── SubCategory.java
│   │   └── movement/
│   │       └── Movement.java
│   ├── service/                        # Domain Services
│   │   ├── BorrowDomainService.java
│   │   ├── MaterialStockService.java
│   │   └── UserValidationService.java
│   └── port/                           # CAPA 2: PORTS
│       ├── in/                         # Input Ports (UseCases)
│       │   ├── borrow/
│       │   │   ├── CreateBorrowUseCase.java
│       │   │   ├── GetBorrowsUseCase.java
│       │   │   └── UpdateBorrowStatusUseCase.java
│       │   ├── material/
│       │   │   ├── CreateMaterialUseCase.java
│       │   │   ├── GetMaterialsUseCase.java
│       │   │   └── UpdateMaterialUseCase.java
│       │   └── user/
│       │       ├── RegisterUserUseCase.java
│       │       └── AuthenticateUserUseCase.java
│       └── out/                        # Output Ports (Repositories)
│           ├── BorrowRepositoryPort.java    ✅ YA EXISTE
│           ├── MaterialsRepositoryPort.java ✅ YA EXISTE
│           ├── UsuarioRepositoryPort.java   ✅ YA EXISTE
│           ├── CategoryRepositoryPort.java
│           ├── MovementRepositoryPort.java
│           └── EmailServicePort.java
│
├── application/                         # CAPA 2: APLICACIÓN
│   └── usecase/                        # Implementación de Use Cases
│       ├── borrow/
│       │   ├── CreateBorrowUseCaseImpl.java
│       │   ├── GetBorrowsUseCaseImpl.java  ✅ YA EXISTE
│       │   └── UpdateBorrowStatusUseCaseImpl.java ✅ YA EXISTE
│       ├── material/
│       │   ├── CreateMaterialUseCaseImpl.java
│       │   ├── GetMaterialsUseCaseImpl.java
│       │   └── UpdateMaterialUseCaseImpl.java
│       └── user/
│           ├── RegisterUserUseCaseImpl.java
│           └── AuthenticateUserUseCaseImpl.java
│
└── infra/                              # CAPA 3: INFRAESTRUCTURA
    ├── adapter/                        # Adapters
    │   ├── input/                      # Input Adapters (Controllers)
    │   │   ├── rest/
    │   │   │   ├── BorrowController.java
    │   │   │   ├── MaterialController.java
    │   │   │   ├── UserController.java
    │   │   │   └── AuthController.java
    │   │   └── dto/                    # DTOs para API
    │   │       ├── BorrowDTO.java
    │   │       ├── MaterialDTO.java
    │   │       └── UserDTO.java
    │   └── output/                     # Output Adapters
    │       ├── jpa/                    # JPA Adapters
    │       │   ├── JpaBorrowRepositoryAdapter.java      ✅ YA EXISTE
    │       │   ├── JpaMaterialsRepositoryAdapter.java   ✅ YA EXISTE
    │       │   ├── JpaUsuarioRepositoryAdapter.java     ✅ YA EXISTE
    │       │   ├── JpaCategoryRepositoryAdapter.java
    │       │   └── JpaMovementRepositoryAdapter.java
    │       ├── email/                  # Email Service Adapter
    │       │   └── SmtpEmailAdapter.java
    │       └── storage/                # Storage Service Adapter
    │           ├── MinioStorageAdapter.java
    │           └── S3StorageAdapter.java
    ├── mapper/                         # Mappers (Domain <-> JPA Entity)
    │   ├── DomainBorrowMapper.java    ✅ YA EXISTE
    │   ├── DomainMaterialMapper.java
    │   ├── DomainUserMapper.java
    │   └── DomainMovementMapper.java
    └── entity/                         # JPA Entities (Infrastructure)
        ├── BorrowEntity.java          # Migrar de entity/ a infra/entity/
        ├── MaterialEntity.java
        ├── UserEntity.java
        └── MovementEntity.java
```

---

## 🔄 Plan de Migración por Módulos

### ✅ COMPLETADO: Módulo Borrow

- [x] Domain models (Borrow, DetailsBorrow, Material)
- [x] Ports (BorrowRepositoryPort, etc.)
- [x] Use Cases (Create, Get, UpdateStatus)
- [x] JPA Adapters
- [x] Mappers (Domain ↔ JPA)

### 🔄 EN PROGRESO: Módulo Materials

#### Paso 1: Domain Layer

- [ ] Crear `domain/model/material/Material.java` (domain model puro)
- [ ] Crear `domain/model/material/Category.java`
- [ ] Crear `domain/model/material/SubCategory.java`
- [ ] Crear `domain/model/material/MaterialId.java` (Value Object)
- [ ] Crear `domain/service/MaterialStockService.java` (business rules)

#### Paso 2: Ports

- [ ] `domain/port/in/CreateMaterialUseCase.java` (interface)
- [ ] `domain/port/in/GetMaterialsUseCase.java` (interface)
- [ ] `domain/port/in/UpdateMaterialUseCase.java` (interface)
- [ ] `domain/port/out/MaterialRepositoryPort.java` (interface)
- [ ] `domain/port/out/CategoryRepositoryPort.java` (interface)

#### Paso 3: Application Layer

- [ ] `application/usecase/material/CreateMaterialUseCaseImpl.java`
- [ ] `application/usecase/material/GetMaterialsUseCaseImpl.java`
- [ ] `application/usecase/material/UpdateMaterialUseCaseImpl.java`

#### Paso 4: Infrastructure Layer

- [ ] `infra/adapter/output/jpa/JpaMaterialRepositoryAdapter.java`
- [ ] `infra/adapter/output/jpa/JpaCategoryRepositoryAdapter.java`
- [ ] `infra/mapper/DomainMaterialMapper.java`
- [ ] Refactor `controller/MaterialsController.java` → usar Use Cases

### 📋 PENDIENTE: Módulo User/Auth

#### Paso 1: Domain Layer

- [ ] `domain/model/user/User.java` (domain model)
- [ ] `domain/model/user/UserId.java` (Value Object)
- [ ] `domain/model/user/Email.java` (Value Object)
- [ ] `domain/model/user/Password.java` (Value Object - hash)
- [ ] `domain/model/user/Role.java` (Value Object)
- [ ] `domain/service/UserValidationService.java`
- [ ] `domain/service/AuthenticationService.java`

#### Paso 2: Ports

- [ ] `domain/port/in/RegisterUserUseCase.java`
- [ ] `domain/port/in/AuthenticateUserUseCase.java`
- [ ] `domain/port/in/GetUserUseCase.java`
- [ ] `domain/port/out/UserRepositoryPort.java`
- [ ] `domain/port/out/RoleRepositoryPort.java`
- [ ] `domain/port/out/EmailServicePort.java`

#### Paso 3: Application Layer

- [ ] `application/usecase/user/RegisterUserUseCaseImpl.java`
- [ ] `application/usecase/user/AuthenticateUserUseCaseImpl.java`
- [ ] `application/usecase/user/GetUserUseCaseImpl.java`

#### Paso 4: Infrastructure Layer

- [ ] `infra/adapter/output/jpa/JpaUserRepositoryAdapter.java`
- [ ] `infra/adapter/output/jpa/JpaRoleRepositoryAdapter.java`
- [ ] `infra/adapter/output/email/SmtpEmailAdapter.java`
- [ ] `infra/mapper/DomainUserMapper.java`
- [ ] Refactor `controller/AuthController.java`

### 📋 PENDIENTE: Módulo Movements

#### Paso 1: Domain Layer

- [ ] `domain/model/movement/Movement.java`
- [ ] `domain/model/movement/MovementType.java` (enum)

#### Paso 2: Ports

- [ ] `domain/port/in/CreateMovementUseCase.java`
- [ ] `domain/port/in/GetMovementsUseCase.java`
- [ ] `domain/port/out/MovementRepositoryPort.java`

#### Paso 3: Application & Infrastructure

- [ ] Use Cases implementation
- [ ] JPA Adapters
- [ ] Refactor controllers

### 📋 PENDIENTE: Módulo Categories

#### Domain & Ports

- [ ] `domain/model/category/Category.java`
- [ ] `domain/model/category/SubCategory.java`
- [ ] Ports & Use Cases
- [ ] Adapters

---

## 🛠️ Refactoring Estrategia

### Fase 1: Preparación (Sin Breaking Changes)

1. Crear estructura hexagonal en paralelo
2. Domain models coexisten con JPA entities
3. Use Cases consumen Services actuales

### Fase 2: Migración Gradual (Módulo por Módulo)

1. Migrar Materials (más simple)
2. Migrar User/Auth (más complejo)
3. Migrar Movements
4. Migrar Categories

### Fase 3: Limpieza (Eliminar Legacy)

1. Eliminar `service/` antigua
2. Consolidar `entity/` → `infra/entity/`
3. Mover `dto/` → `infra/adapter/input/dto/`
4. Actualizar tests

---

## 📋 Checklist por Módulo

### Para cada módulo:

- [ ] ✅ Domain models sin dependencias de framework
- [ ] ✅ Ports (in/out) como interfaces puras
- [ ] ✅ Use Cases implementan lógica de orquestación
- [ ] ✅ Adapters JPA implementan ports
- [ ] ✅ Mappers Domain ↔ JPA Entity
- [ ] ✅ Controllers usan Use Cases (no Services directamente)
- [ ] ✅ Tests unitarios para Use Cases
- [ ] ✅ Tests de integración para Adapters
- [ ] ✅ Documentación actualizada

---

## 🎯 Objetivos de Calidad

### Métricas

- **Cobertura de tests**: ≥ 80%
- **Acoplamiento**: Domain layer = 0 dependencias externas
- **Cohesión**: Alta (cada capa tiene responsabilidad clara)
- **Complejidad ciclomática**: ≤ 10 por método

### Validaciones

```bash
# Verificar que Domain no tiene dependencias de Spring/JPA
find src/main/java/com/techmate/techmate/domain -name "*.java" | xargs grep -l "import org.springframework"
# Output esperado: vacío

# Verificar que Domain no tiene @Entity
find src/main/java/com/techmate/techmate/domain -name "*.java" | xargs grep -l "@Entity"
# Output esperado: vacío

# Compilar sin errores
mvn clean compile

# Tests pasan
mvn test
```

---

## 📚 Referencias

- **Onion Architecture**: Jeffrey Palermo
- **Hexagonal Architecture**: Alistair Cockburn (Ports & Adapters)
- **Clean Architecture**: Robert C. Martin
- **Domain-Driven Design**: Eric Evans

---

**Inicio de Migración**: 28 Diciembre 2025  
**Arquitecto**: GitHub Copilot - Senior Software Architect  
**Estado**: 🔄 EN PROGRESO (25% completado - solo Borrow)
