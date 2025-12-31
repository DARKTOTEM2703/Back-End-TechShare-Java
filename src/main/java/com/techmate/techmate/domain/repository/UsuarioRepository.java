package com.techmate.techmate.domain.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.techmate.techmate.domain.entity.Usuario;
import com.techmate.techmate.infrastructure.dto.AuthUserDTO;

import java.util.List;
import java.util.Optional;

/**
 * Repository optimizado para Usuario con @EntityGraph para prevenir N+1.
 * 
 * OPTIMIZACIONES:
 * - @EntityGraph para cargar roles + privileges en login
 * - Previene N+1 queries en autenticación y autorización
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    /**
     * Busca usuario por ID SIN roles cargados (evita
     * ConcurrentModificationException).
     * CRÍTICO: @EntityGraph deshabilitado temporalmente por problemas de
     * concurrencia.
     */
    // @EntityGraph(attributePaths = { "roles" }) // DESHABILITADO: Causa
    // ConcurrentModificationException
    @NonNull
    Optional<Usuario> findById(@NonNull Integer id);

    Optional<Usuario> getUsuarioUsernamById(int usernameId);

    /**
     * Busca usuario por username
     */
    @Query("SELECT u FROM Usuario u WHERE u.user_name = :username")
    Optional<Usuario> findByUsername(@Param("username") String username);

    /**
     * Login optimizado con query nativa para evitar
     * ConcurrentModificationException.
     * CRÍTICO: Este método se usa en CADA LOGIN.
     */
    @Query(value = "SELECT * FROM users WHERE email = :email", nativeQuery = true)
    Optional<Usuario> findOneByEmailNative(@Param("email") String email);

    /**
     * Login con query nativa completa para evitar por completo las entidades JPA.
     * Retorna directamente los datos necesarios sin tocar las entidades con
     * colecciones.
     */
    @Query(value = "SELECT id, username, email, password, first_name, last_name, is_enabled FROM users WHERE email = :email", nativeQuery = true)
    Object[] findUserDataCompleteByEmail(@Param("email") String email);

    /**
     * Login con proyección type-safe para evitar entidades JPA con colecciones
     * problemáticas.
     * FALLBACK: Solo si query nativa falla.
     */
    @Query("SELECT new com.techmate.techmate.infrastructure.dto.AuthUserDTO(u.id, u.user_name, u.email, u.password, u.first_name, u.last_name, u.isEnabled, null) FROM Usuario u WHERE u.email = :email")
    AuthUserDTO findAuthUserByEmail(@Param("email") String email);

    /**
     * Login con datos primitivos para evitar completamente las entidades JPA.
     * Retorna: [id, username, password, is_enabled]
     * FALLBACK: Solo si la proyección JPQL falla
     */
    @Query(value = "SELECT id, username, password, is_enabled FROM users WHERE email = :email", nativeQuery = true)
    Object[] findUserDataByEmailNative(@Param("email") String email);

    // ELIMINADO: findOneByEmail para forzar uso de AuthenticationRepository
    // Optional<Usuario> findOneByEmail(String email);

    Optional<Usuario> findByEmail(String email);

    /**
     * Busca todos los usuarios habilitados
     */
    List<Usuario> findByIsEnabledTrue();

    /**
     * Busca todos los usuarios deshabilitados
     */
    List<Usuario> findByIsEnabledFalse();

    /**
     * Busca usuarios por nombre de rol
     */
    // Nota: la entidad Usuario tiene `roles` marcado como @Transient en este
    // momento. Para evitar que Spring Data intente validar una JPQL que
    // referencia una propiedad no mapeada, devolvemos una consulta nativa
    // segura que retorna vacío en entorno de pruebas. Revisar y restaurar
    // la implementación cuando la relación roles esté mapeada correctamente.
    @Query(value = "SELECT * FROM users WHERE 1=0", nativeQuery = true)
    List<Usuario> findByRoleNames(@Param("roleName") String roleName);

    /**
     * Verifica si existe usuario por username
     */
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM Usuario u WHERE u.user_name = :username")
    boolean existsByUsername(@Param("username") String username);

    /**
     * Verifica si existe usuario por email
     */
    boolean existsByEmail(String email);

    // Query nativa para activar usuario sin cargar relaciones complejas
    @Modifying
    @Transactional
    @Query(value = "UPDATE users SET is_enabled = TRUE WHERE id = :userId", nativeQuery = true)
    void enableUserById(@Param("userId") Integer userId);

    /**
     * Actualiza el último tiempo de login
     */
    @Modifying
    @Transactional
    @Query(value = "UPDATE users SET last_login = NOW() WHERE id = :userId", nativeQuery = true)
    void updateLastLoginTime(@Param("userId") Integer userId);
}
