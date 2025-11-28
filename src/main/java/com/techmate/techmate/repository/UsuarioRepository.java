package com.techmate.techmate.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.techmate.techmate.entity.Usuario;

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
     * Busca usuario por ID con roles cargados (1 query).
     * EVITA N+1: Usuario + roles en una sola query
     */
    @EntityGraph(attributePaths = {"roles"})
    @NonNull
    Optional<Usuario> findById(@NonNull Integer id);

    Optional<Usuario> getUsuarioUsernamById(int usernameId);

    /**
     * Login optimizado: carga usuario + roles + privileges en 1 query.
     * CRÍTICO: Este método se usa en CADA LOGIN.
     * EVITA: N+1 al verificar permisos durante autenticación.
     */
    @EntityGraph(attributePaths = {"roles", "roles.privileges"})
    Optional<Usuario> findOneByEmail(String email);

    Optional<Usuario> findByEmail(String email);

    // Query nativa para activar usuario sin cargar relaciones complejas
    @Modifying
    @Transactional
    @Query(value = "UPDATE users SET is_enabled = TRUE WHERE id = :userId", nativeQuery = true)
    void enableUserById(@Param("userId") Integer userId);

}
