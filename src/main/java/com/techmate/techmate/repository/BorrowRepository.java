package com.techmate.techmate.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.techmate.techmate.entity.Borrow;
import com.techmate.techmate.entity.Status;

/**
 * Repository optimizado para Borrow con queries que previenen N+1.
 * 
 * MEJORAS IMPLEMENTADAS:
 * - JOIN FETCH para cargar relaciones en 1 sola query
 * - Métodos optimizados con paginación
 * - Filtros dinámicos eficientes
 */
@Repository
public interface BorrowRepository extends JpaRepository<Borrow, Integer> {
    
    // ============================================
    // QUERIES OPTIMIZADAS CON JOIN FETCH
    // ============================================
    
    /**
     * Obtiene todos los préstamos con sus relaciones cargadas (1 query).
     * EVITA: N+1 problem
     * ANTES: 1 + N (details) + M (materials) + K (subcategories) queries
     * AHORA: 1 query con joins
     */
    @Query("SELECT DISTINCT b FROM Borrow b " +
           "LEFT JOIN FETCH b.details d " +
           "LEFT JOIN FETCH d.materials m " +
           "LEFT JOIN FETCH m.subCategory " +
           "LEFT JOIN FETCH b.usuario")
    List<Borrow> findAllOptimized();
    
    /**
     * Obtiene un préstamo por ID con todas sus relaciones (1 query).
     */
    @Query("SELECT b FROM Borrow b " +
           "LEFT JOIN FETCH b.details d " +
           "LEFT JOIN FETCH d.materials m " +
           "LEFT JOIN FETCH m.subCategory " +
           "LEFT JOIN FETCH b.usuario " +
           "WHERE b.id = :id")
    Optional<Borrow> findByIdOptimized(@Param("id") Integer id);
    
    /**
     * Paginación optimizada con JOIN FETCH.
     * countQuery separada para evitar joins innecesarios en el COUNT.
     */
       @Query(value = "SELECT DISTINCT b FROM Borrow b " +
                             "LEFT JOIN FETCH b.details d " +
                             "LEFT JOIN FETCH d.materials m " +
                             "LEFT JOIN FETCH m.subCategory " +
                             "LEFT JOIN FETCH b.usuario ",
          countQuery = "SELECT COUNT(DISTINCT b) FROM Borrow b")
    Page<Borrow> findAllOptimizedPaginated(Pageable pageable);
    
    /**
     * Búsqueda por usuario optimizada.
     */
    @Query("SELECT DISTINCT b FROM Borrow b " +
           "LEFT JOIN FETCH b.details d " +
           "LEFT JOIN FETCH d.materials m " +
           "LEFT JOIN FETCH m.subCategory " +
           "LEFT JOIN FETCH b.usuario u " +
           "WHERE u.id = :usuarioId")
    List<Borrow> findByUsuarioIdOptimized(@Param("usuarioId") Integer usuarioId);
    
    /**
     * Búsqueda por estado optimizada.
     */
    @Query("SELECT DISTINCT b FROM Borrow b " +
           "LEFT JOIN FETCH b.details d " +
           "LEFT JOIN FETCH d.materials m " +
           "LEFT JOIN FETCH m.subCategory " +
           "LEFT JOIN FETCH b.usuario " +
           "WHERE b.status = :status")
    List<Borrow> findByStatusOptimized(@Param("status") Status status);
    
    /**
     * Búsqueda por rango de fechas optimizada.
     */
    @Query("SELECT DISTINCT b FROM Borrow b " +
           "LEFT JOIN FETCH b.details d " +
           "LEFT JOIN FETCH d.materials m " +
           "LEFT JOIN FETCH m.subCategory " +
           "LEFT JOIN FETCH b.usuario " +
           "WHERE b.date BETWEEN :startDate AND :endDate")
    List<Borrow> findByDateBetweenOptimized(
        @Param("startDate") Date startDate, 
        @Param("endDate") Date endDate
    );
    
    /**
     * Filtros combinados optimizados.
     */
    @Query("SELECT DISTINCT b FROM Borrow b " +
           "LEFT JOIN FETCH b.details d " +
           "LEFT JOIN FETCH d.materials m " +
           "LEFT JOIN FETCH m.subCategory " +
           "LEFT JOIN FETCH b.usuario " +
           "WHERE b.status = :status " +
           "AND b.date BETWEEN :startDate AND :endDate")
    List<Borrow> findByStatusAndDateBetweenOptimized(
        @Param("status") Status status,
        @Param("startDate") Date startDate, 
        @Param("endDate") Date endDate
    );
    
    /**
     * Filtros dinámicos con paginación (nullable parameters).
     * Permite búsquedas flexibles sin crear múltiples métodos.
     */
    @Query(value = "SELECT DISTINCT b FROM Borrow b " +
                   "LEFT JOIN FETCH b.details d " +
                   "LEFT JOIN FETCH d.materials m " +
                   "LEFT JOIN FETCH m.subCategory " +
                   "LEFT JOIN FETCH b.usuario u " +
                   "WHERE (:usuarioId IS NULL OR u.id = :usuarioId) " +
                   "AND (:status IS NULL OR b.status = :status) " +
                   "AND (:startDate IS NULL OR b.date >= :startDate) " +
                   "AND (:endDate IS NULL OR b.date <= :endDate)",
           countQuery = "SELECT COUNT(DISTINCT b) FROM Borrow b " +
                       "LEFT JOIN b.usuario u " +
                       "WHERE (:usuarioId IS NULL OR u.id = :usuarioId) " +
                       "AND (:status IS NULL OR b.status = :status) " +
                       "AND (:startDate IS NULL OR b.date >= :startDate) " +
                       "AND (:endDate IS NULL OR b.date <= :endDate)")
    Page<Borrow> findByFiltersOptimized(
        @Param("usuarioId") Integer usuarioId,
        @Param("status") Status status,
        @Param("startDate") Date startDate,
        @Param("endDate") Date endDate,
        Pageable pageable
    );
    
    // ============================================
    // MÉTODOS LEGACY (mantener compatibilidad)
    // ============================================
    // NOTA: Estos métodos causan N+1, usa los *Optimized cuando sea posible
    
    List<Borrow> findByUsuarioId(Integer usuarioId);
    List<Borrow> findByStatus(Status status);
    List<Borrow> findByDateBetween(Date startDate, Date endDate);
    List<Borrow> findByStatusAndDateBetween(Status status, Date startDate, Date endDate);
    
    // ============================================
    // MÉTODOS PARA HEALTH CHECKS
    // ============================================
    
    /**
     * Cuenta préstamos por estado.
     * Usado por BorrowSystemHealthIndicator para monitoreo.
     */
    long countByStatus(Status status);
}


