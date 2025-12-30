package com.techmate.techmate.hexagonal.domain.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.techmate.techmate.hexagonal.domain.entity.Borrow;
import com.techmate.techmate.hexagonal.domain.entity.Status;

/**
 * Repository optimizado para Borrow con queries que previenen N+1.
 * 
 * MEJORAS IMPLEMENTADAS:
 * - JOIN FETCH para cargar relaciones en 1 sola query
 * - Métodos optimizados con paginación
 * - Filtros dinámicos eficientes
 * - JpaSpecificationExecutor para queries type-safe composables (ITERACIÓN 3B)
 * 
 * SPECIFICATION PATTERN (NUEVO):
 * Usa BorrowSpecification para queries dinámicas type-safe:
 * 
 * <pre>
 * List<Borrow> results = borrowRepository.findAll(
 *               Specification.where(BorrowSpecification.byStatus(Status.BORROWED))
 *                             .and(BorrowSpecification.byDateRange(start, end)));
 * </pre>
 * 
 * @see com.techmate.techmate.hexagonal.domain.repository.specification.BorrowSpecification
 */
@Repository
public interface BorrowRepository extends JpaRepository<Borrow, Integer>,
              JpaSpecificationExecutor<Borrow> {

       // ============================================
       // QUERIES OPTIMIZADAS CON JOIN FETCH
       // ============================================

       /**
        * Sobrescribimos `findAll()` de `JpaRepository` para devolver siempre
        * los `Borrow` con sus relaciones necesarias cargadas y evitar N+1.
        */
       @Query("SELECT DISTINCT b FROM Borrow b " +
                     "LEFT JOIN FETCH b.details d " +
                     "LEFT JOIN FETCH d.materials m " +
                     "LEFT JOIN FETCH m.subCategory " +
                     "LEFT JOIN FETCH b.usuario")
       @Override
       List<Borrow> findAll();

       /**
        * Sobrescribimos `findById(Integer)` de `JpaRepository` para devolver
        * la entidad con relaciones cargadas y evitar cargas perezosas fuera de
        * la transacción.
        */
       @Query("SELECT b FROM Borrow b " +
                     "LEFT JOIN FETCH b.details d " +
                     "LEFT JOIN FETCH d.materials m " +
                     "LEFT JOIN FETCH m.subCategory " +
                     "LEFT JOIN FETCH b.usuario " +
                     "WHERE b.id = :id")
       @Override
       Optional<Borrow> findById(@Param("id") Integer id);

       /**
        * Paginación optimizada con JOIN FETCH.
        * countQuery separada para evitar joins innecesarios en el COUNT.
        */
       @Query(value = "SELECT DISTINCT b FROM Borrow b " +
                     "LEFT JOIN FETCH b.details d " +
                     "LEFT JOIN FETCH d.materials m " +
                     "LEFT JOIN FETCH m.subCategory " +
                     "LEFT JOIN FETCH b.usuario ", countQuery = "SELECT COUNT(DISTINCT b) FROM Borrow b")
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
                     @Param("endDate") Date endDate);

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
                     @Param("endDate") Date endDate);

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
                     "AND (:endDate IS NULL OR b.date <= :endDate)", countQuery = "SELECT COUNT(DISTINCT b) FROM Borrow b "
                                   +
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
                     Pageable pageable);

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







