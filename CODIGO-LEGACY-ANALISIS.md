# 📋 Análisis de Código Legacy en TechShare Backend

**Fecha:** 28 de noviembre de 2025  
**Estado del Proyecto:** JWT Authentication implementado ✅  
**Última migración:** Autenticación con AuthenticationRepository y AuthUserDTO

---

## 🔍 Resumen Ejecutivo

El backend de TechShare mantiene **nombres de entidades legacy** (`Usuario`, `UsuarioRole`) mientras que la base de datos usa nombres normalizados (`users`, `user_role`). Esto es **intencional y funcional** gracias a las anotaciones JPA `@Table` y `@Column`.

**Estado Actual:** ✅ **Sistema funcional** - Las entidades legacy están correctamente mapeadas

---

## 🗂️ Entidades Legacy vs Normalizadas

### ✅ Entidades que usan nombres legacy (23 archivos)

| Entidad Legacy | Tabla BD Normalizada | Mapeo JPA | Estado |
|----------------|---------------------|-----------|--------|
| `Usuario` | `users` | `@Table(name = "users")` | ✅ Funcional |
| `UsuarioRole` | `user_role` | `@Table(name = "user_role")` | ✅ Funcional |
| `Role` | `roles` | `@Table(name = "roles")` | ✅ Funcional |

### 📁 Archivos que usan entidades legacy:

#### 🔐 **Autenticación y Seguridad (6 archivos)**
1. `UsuarioRepository.java` - Repository JPA (usa entidad `Usuario`)
2. `UserDetailsServiceImpl.java` - Carga usuario desde BD
3. `UserDetailsImpl.java` - Wrapper de seguridad
4. `AuthService.java` - Servicio de registro y verificación
5. `VerificationService.java` - Verificación de email
6. `AuthMapper.java` - Mapeo de DTOs

#### 👥 **Servicios de Usuario (5 archivos)**
7. `UserServiceImpl.java` - CRUD de usuarios
8. `UserQueryService.java` - Consultas optimizadas
9. `UserMapper.java` - Conversión entity ↔ DTO
10. `UserDemoServiceImpl.java` - Servicio demo
11. `BorrowUserServiceImp.java` - Préstamos de usuario

#### 📦 **Préstamos (Borrow) (4 archivos)**
12. `BorrowServiceImpl.java` - Lógica de préstamos
13. `BorrowStateProcessor.java` - Procesamiento de estados
14. `BorrowStockManager.java` - Gestión de stock
15. `IBorrowStockManager.java` - Interfaz de stock

#### 📊 **Movimientos (2 archivos)**
16. `MovementsServiceImpl.java` - Movimientos de inventario
17. `MovementMapper.java` - Mapeo de movimientos

#### 🔗 **Relaciones y Roles (3 archivos)**
18. `UsuarioRoleRepository.java` - Repository de user_role
19. `RoleAssociationManager.java` - Gestión de roles
20. `RoleRepository.java` - Repository de roles

#### 📝 **DTOs (3 archivos)**
21. `RegisterRequest.java` - DTO de registro (usa `Usuario.Gender`)
22. `UsuarioDTO.java` - DTO de usuario
23. `CurrentUserDTO.java` - DTO de usuario actual

---

## ✅ Estrategia de Migración COMPLETA (Nueva Arquitectura)

### 🎯 **Solución Implementada: Dual Repository Pattern**

Para evitar problemas de JPA/Hibernate (ConcurrentModificationException, lazy loading), se implementó:

#### 1. **AuthenticationRepository** (✅ NUEVO)
```java
@Repository
public class AuthenticationRepository {
    private final JdbcTemplate jdbcTemplate;
    
    // Query directo SIN JPA - retorna AuthUserDTO
    public AuthUserDTO findUserByEmailForAuth(String email) {
        String sql = "SELECT id, username, email, password, first_name, last_name, is_enabled " +
                    "FROM users WHERE email = ?";
        return jdbcTemplate.queryForObject(sql, ...);
    }
}
```

**Ventajas:**
- ✅ Sin lazy loading
- ✅ Sin ConcurrentModificationException
- ✅ Control total sobre queries
- ✅ Performance óptimo

#### 2. **AuthUserDTO** (✅ NUEVO)
```java
public class AuthUserDTO {
    private Integer id;
    private String username;
    private String email;
    private String password;
    private List<String> roleNames; // Solo nombres, no entidades
}
```

**Ventajas:**
- ✅ Inmutable
- ✅ Sin referencias a entidades JPA
- ✅ Type-safe
- ✅ Serializable

#### 3. **UserDetailsImpl actualizado** (✅ MEJORADO)
```java
public UserDetailsImpl(AuthUserDTO authUser) {
    this.userId = authUser.getId();
    this.email = authUser.getEmail();
    this.password = authUser.getPassword();
    this.userName = authUser.getUsername();
    this.roleNames = authUser.getRoleNames();
}

// DEPRECATED: getUsuario() 
@Deprecated
public Usuario getUsuario() {
    throw new UnsupportedOperationException("Use getId(), getEmail() directly");
}
```

**Cambios:**
- ✅ Constructor acepta `AuthUserDTO` (no `Usuario`)
- ✅ Métodos directos: `getId()`, `getEmail()`, `isEnabled()`
- ✅ `getUsuario()` marcado como `@Deprecated`

---

## 🚨 Código Legacy que SÍ necesita actualización

### ❌ **AuthenticatedUserController.java**

**Problema actual:**
```java
currentUser.setUser_name(userDetails.getNombre()); // ❌ usa getNombre()
```

**Debería ser:**
```java
currentUser.setUser_name(userDetails.getUserName()); // ✅ método correcto
```

**Impacto:** El sidebar del frontend no muestra el nombre correcto del usuario.

**Fix necesario:** Actualizar línea 51 en `AuthenticatedUserController.java`

---

## 📊 Métricas de Código Legacy

| Categoría | Archivos Afectados | Estado | Prioridad |
|-----------|-------------------|--------|-----------|
| **Autenticación** | 6 | ✅ Migrado a JDBC | Completado |
| **Servicios Usuario** | 5 | ⚠️ Funcional con legacy | Baja |
| **Préstamos** | 4 | ✅ Funcional | Media |
| **Repositorios** | 3 | ⚠️ Mix legacy/nuevo | Baja |
| **DTOs** | 3 | ✅ Funcional | Baja |

**Total:** 23 archivos con nombres legacy (100% funcional)

---

## 🎯 Recomendaciones

### ✅ **Mantener nombres legacy en entidades**
**Razón:** 
- JPA mapea correctamente con `@Table` y `@Column`
- Refactorización masiva no aporta valor funcional
- Riesgo de romper código existente es alto

### ✅ **Continuar con patrón AuthenticationRepository**
**Razón:**
- Resuelve problemas de Hibernate definitivamente
- Performance superior a JPA
- Más control sobre queries

### ⚠️ **Migración gradual recomendada (Opcional)**

Si se decide migrar entidades legacy → normalized:

1. **Fase 1:** Crear entidades `User` (alias de `Usuario`)
   ```java
   @Entity
   @Table(name = "users")
   public class User { // Nuevo nombre
       // Mismos campos que Usuario
   }
   ```

2. **Fase 2:** Dual repository (legacy + nuevo)
   ```java
   public interface UserRepository extends JpaRepository<User, Integer> {}
   public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {}
   ```

3. **Fase 3:** Migrar servicios uno por uno

4. **Fase 4:** Deprecar `Usuario` cuando todos los servicios usen `User`

**Estimación:** 2-3 sprints (riesgo moderado)

---

## 🛠️ Acciones Inmediatas Requeridas

### 🔴 **CRÍTICO - Arreglar sidebar usuario**

**Archivo:** `AuthenticatedUserController.java`  
**Línea:** 51  
**Cambio:**
```java
// ANTES (❌)
currentUser.setUser_name(userDetails.getNombre());

// DESPUÉS (✅)
currentUser.setUser_name(userDetails.getUserName());
```

### 🟡 **MEDIO - Arreglar display de préstamos**

**Archivo:** `TechShare-FrontEnd/src/app/admin/borrowings/page.tsx`  
**Línea:** 31-33  
**Cambio:**
```tsx
// ANTES (❌)
const filteredData = data.filter(
    (borrowing: Borrow) => !borrowing.status.includes("PROCCES") // Typo
);

// DESPUÉS (✅)
// Mostrar todos los préstamos sin filtrar por typo inexistente
setData(data);
```

---

## 📈 Estado del Proyecto POST-Migración JWT

### ✅ **Completado**
- [x] JWT Authentication con Spring Security
- [x] Fix ConcurrentModificationException
- [x] AuthenticationRepository con JdbcTemplate
- [x] AuthUserDTO sin dependencias JPA
- [x] UserDetailsImpl mejorado
- [x] Migración SQL con usuario admin por defecto
- [x] Tests actualizados

### ⏳ **Pendiente**
- [ ] Fix sidebar usuario (getNombre → getUserName)
- [ ] Fix display préstamos frontend (quitar filtro "PROCCES")
- [ ] Servicio de verificación email (funcional pero usa legacy)
- [ ] Refresh tokens (mejora de seguridad)
- [ ] Rate limiting (producción)

---

## 🎓 Conclusiones

1. **Nombres legacy NO son un problema** - JPA mapea correctamente
2. **Dual Repository Pattern es exitoso** - Sin problemas de Hibernate
3. **Migración completa es opcional** - No aporta valor inmediato
4. **Prioridad:** Arreglar bugs funcionales (sidebar, préstamos) antes que refactorizar nombres

**Recomendación final:** Mantener arquitectura actual y enfocarse en funcionalidad y testing.
