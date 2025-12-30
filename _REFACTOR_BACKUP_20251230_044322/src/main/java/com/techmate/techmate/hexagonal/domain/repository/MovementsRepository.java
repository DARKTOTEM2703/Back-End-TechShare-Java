package com.techmate.techmate.hexagonal.domain.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.techmate.techmate.hexagonal.domain.entity.MoveType;
import com.techmate.techmate.hexagonal.domain.entity.Movements;

/**
 * Repository optimizado para Movements con queries que previenen N+1.
 * 
 * MEJORAS IMPLEMENTADAS:
 * - JOIN FETCH para cargar relaciones en 1 sola query
 * - Paginación optimizada
 * - Queries para estadísticas agregadas
 */
public interface MovementsRepository extends JpaRepository<Movements, Integer> {
    
    // ============================================
    // QUERIES OPTIMIZADAS CON JOIN FETCH
    // ============================================
    
    /**
     * Obtiene todos los movimientos con sus relaciones cargadas (1 query).
     * EVITA: N+1 problem
     * CARGA: Materials + subCategory + usuario en 1 query
     */
        @Query("SELECT DISTINCT m FROM Movements m " +
            "LEFT JOIN FETCH m.materials mat " +
            "LEFT JOIN FETCH mat.subCategory " +
            "LEFT JOIN FETCH m.usuario")
        @Override
        List<Movements> findAll();
    
    /**
     * Obtiene un movimiento por ID con todas sus relaciones (1 query).
     */
    @Query("SELECT m FROM Movements m " +
           "LEFT JOIN FETCH m.materials mat " +
           "LEFT JOIN FETCH mat.subCategory " +
           "LEFT JOIN FETCH m.usuario " +
        "WHERE m.id = :id")
    @Override
    Optional<Movements> findById(@Param("id") Integer id);
    
    /**
     * Paginación optimizada con JOIN FETCH.
     */
    @Query(value = "SELECT DISTINCT m FROM Movements m " +
                   "LEFT JOIN FETCH m.materials mat " +
                   "LEFT JOIN FETCH mat.subCategory " +
                   "LEFT JOIN FETCH m.usuario",
           countQuery = "SELECT COUNT(DISTINCT m) FROM Movements m")
    Page<Movements> findAllOptimizedPaginated(Pageable pageable);
    
    /**
     * Búsqueda por tipo de movimiento optimizada.
     */
    @Query("SELECT DISTINCT m FROM Movements m " +
           "LEFT JOIN FETCH m.materials mat " +
           "LEFT JOIN FETCH mat.subCategory " +
           "LEFT JOIN FETCH m.usuario " +
           "WHERE m.moveType = :moveType")
    List<Movements> findByMoveTypeOptimized(@Param("moveType") MoveType moveType);
    
    /**
     * Búsqueda por rango de fechas optimizada.
     */
    @Query("SELECT DISTINCT m FROM Movements m " +
           "LEFT JOIN FETCH m.materials mat " +
           "LEFT JOIN FETCH mat.subCategory " +
           "LEFT JOIN FETCH m.usuario " +
           "WHERE m.movementDate BETWEEN :startDate AND :endDate")
    List<Movements> findByDateBetweenOptimized(
        @Param("startDate") Date startDate,
        @Param("endDate") Date endDate
    );
    
    /**
     * Filtros dinámicos con paginación (nullable parameters).
     */
    @Query(value = "SELECT DISTINCT m FROM Movements m " +
                   "LEFT JOIN FETCH m.materials mat " +
                   "LEFT JOIN FETCH mat.subCategory " +
                   "LEFT JOIN FETCH m.usuario u " +
                   "WHERE (:moveType IS NULL OR m.moveType = :moveType) " +
                   "AND (:usuarioId IS NULL OR u.id = :usuarioId) " +
                   "AND (:startDate IS NULL OR m.movementDate >= :startDate) " +
                   "AND (:endDate IS NULL OR m.movementDate <= :endDate)",
           countQuery = "SELECT COUNT(DISTINCT m) FROM Movements m " +
                       "LEFT JOIN m.usuario u " +
                       "WHERE (:moveType IS NULL OR m.moveType = :moveType) " +
                       "AND (:usuarioId IS NULL OR u.id = :usuarioId) " +
                       "AND (:startDate IS NULL OR m.movementDate >= :startDate) " +
                       "AND (:endDate IS NULL OR m.movementDate <= :endDate)")
    Page<Movements> findByFiltersOptimized(
        @Param("moveType") MoveType moveType,
        @Param("usuarioId") Integer usuarioId,
        @Param("startDate") Date startDate,
        @Param("endDate") Date endDate,
        Pageable pageable
    );
    
    // ============================================
    // QUERIES PARA ESTADÍSTICAS (SQL NATIVO)
    // ============================================
    
    /**
     * Obtiene estadísticas mensuales de movimientos.
     * Usa SQL nativo para agregaciones complejas.
     * 
     * RESULTADO: [month, moveType, totalMovements, totalQuantity, uniqueMaterials]
     */
    @Query(value = """
        SELECT 
            DATE_FORMAT(m.movement_date, '%Y-%m') as month,
            m.move_type as moveType,
            COUNT(m.movements_id) as totalMovements,
            SUM(m.quantity) as totalQuantity,
            COUNT(DISTINCT m.materials_id) as uniqueMaterials
        FROM movements m
        WHERE m.movement_date BETWEEN :startDate AND :endDate
        GROUP BY DATE_FORMAT(m.movement_date, '%Y-%m'), m.move_type
        ORDER BY month DESC, moveType
        """, nativeQuery = true)
    List<Object[]> getMonthlyStatistics(
        @Param("startDate") Date startDate,
        @Param("endDate") Date endDate
    );
    
    /**
     * Obtiene top materiales más movidos en un periodo.
     * Nota: Usar Pageable en el servicio para limitar resultados.
     */
    @Query(value = """
        SELECT 
            mat.name as materialName,
            m.move_type as moveType,
            SUM(m.quantity) as totalQuantity,
            COUNT(m.movements_id) as movementCount
        FROM movements m
        INNER JOIN materials mat ON m.materials_id = mat.materials_id
        WHERE m.movement_date BETWEEN :startDate AND :endDate
        GROUP BY mat.materials_id, mat.name, m.move_type
        ORDER BY totalQuantity DESC
        LIMIT 50
        """, nativeQuery = true)
    List<Object[]> getTopMovedMaterials(
        @Param("startDate") Date startDate,
        @Param("endDate") Date endDate
    );
    
    /**
     * Obtiene resumen de movimientos por usuario.
     */
    @Query(value = """
        SELECT 
            u.name as userName,
            m.move_type as moveType,
            COUNT(m.movements_id) as totalMovements,
            SUM(m.quantity) as totalQuantity
        FROM movements m
        INNER JOIN usuario u ON m.usuario_id = u.id
        WHERE m.movement_date BETWEEN :startDate AND :endDate
        GROUP BY u.id, u.name, m.move_type
        ORDER BY totalMovements DESC
        """, nativeQuery = true)
    List<Object[]> getMovementsByUser(
        @Param("startDate") Date startDate,
        @Param("endDate") Date endDate
    );
    
    // ============================================
    // MÉTODOS LEGACY (mantener compatibilidad)
    // ============================================
    // NOTA: Estos métodos pueden causar N+1, usa los *Optimized cuando sea posible
    
    List<Movements> findByMoveType(MoveType moveType);
    List<Movements> findByMovementDateBetween(Date startDate, Date endDate);
}









