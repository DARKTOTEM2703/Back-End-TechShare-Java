package com.techmate.techmate.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.techmate.techmate.entity.Materials;

/**
 * Repository optimizado para Materials con prevención de N+1.
 * 
 * OPTIMIZACIONES:
 * - @EntityGraph para cargar relaciones
 * - Queries con JOIN FETCH
 * - Paginación optimizada
 */
@Repository
public interface MaterialsRepository extends JpaRepository<Materials, Integer> {

    // ============================================
    // QUERIES OPTIMIZADAS CON @EntityGraph
    // ============================================

    /**
     * Busca material por nombre con subcategoría cargada (1 query).
     * EVITA N+1: Materials + SubCategory en 1 sola query
     */
    @EntityGraph(attributePaths = { "subCategory", "subCategory.category" })
    Materials findByName(String name);

    /**
     * Obtiene todos los materiales con subcategorías (1 query).
     * OPTIMIZADO: Usa @EntityGraph para eager loading
     */
    @EntityGraph(attributePaths = { "subCategory", "subCategory.category" })
    @NonNull
    List<Materials> findAll();

    /**
     * Busca material por ID con todas las relaciones (1 query).
     */
    @EntityGraph(attributePaths = { "subCategory", "subCategory.category" })
    @NonNull
    Optional<Materials> findById(@NonNull Integer id);

    // Validación: verifica si existe un material con el nombre dado
    boolean existsByName(String name);

    /**
     * Materiales ordenados por precio ASC con subcategorías.
     */
    @EntityGraph(attributePaths = { "subCategory" })
    List<Materials> findAllByOrderByPriceAsc();

    /**
     * Materiales ordenados por precio DESC con subcategorías.
     */
    @EntityGraph(attributePaths = { "subCategory" })
    List<Materials> findAllByOrderByPriceDesc();

    // ============================================
    // QUERIES ADICIONALES OPTIMIZADAS
    // ============================================

    /**
     * Paginación optimizada de materiales.
     */
    @EntityGraph(attributePaths = { "subCategory", "subCategory.category" })
    @NonNull
    Page<Materials> findAll(@NonNull Pageable pageable);

    /**
     * Busca materiales por categoría con JOIN FETCH.
     */
    @Query("SELECT m FROM Materials m " +
            "LEFT JOIN FETCH m.subCategory sc " +
            "LEFT JOIN FETCH sc.category c " +
            "WHERE c.id = :categoryId")
    List<Materials> findByCategoryIdOptimized(@Param("categoryId") Integer categoryId);

    /**
     * Busca materiales con stock bajo (alerta).
     */
    @EntityGraph(attributePaths = { "subCategory" })
    @Query("SELECT m FROM Materials m WHERE m.stock < :threshold")
    List<Materials> findLowStock(@Param("threshold") int threshold);

}
