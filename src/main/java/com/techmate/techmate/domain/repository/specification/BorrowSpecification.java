package com.techmate.techmate.domain.repository.specification;

import com.techmate.techmate.infrastructure.persistence.entity.Borrow;
import com.techmate.techmate.infrastructure.persistence.entity.Status;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.*;
import java.util.Date;

/**
 * Specification Pattern para Borrow - Queries type-safe y composables.
 * 
 * VENTAJAS:
 * - Type-safe: El compilador valida las queries
 * - Composable: Combinar criterios con and/or
 * - DRY: Reutilización de criterios
 * - Testeable: Mock specifications fácilmente
 * - Performance: Control fino de fetch joins
 * 
 * USAGE:
 * 
 * <pre>
 * List<Borrow> results = borrowRepository.findAll(
 *         Specification.where(BorrowSpecification.byStatus(Status.BORROWED))
 *                 .and(BorrowSpecification.byDateRange(start, end))
 *                 .and(BorrowSpecification.withDetails()));
 * </pre>
 * 
 * @author TechMate Team
 * @since ITERACIÓN 3B
 */
public class BorrowSpecification {

    /**
     * Filtrar por status del préstamo.
     * Null-safe: Si status es null, no aplica filtro.
     * 
     * @param status Estado del préstamo (BORROWED, RETURNED, etc.)
     * @return Specification que filtra por status
     */
    public static Specification<Borrow> byStatus(Status status) {
        return (root, query, criteriaBuilder) -> status == null ? null
                : criteriaBuilder.equal(root.get("status"), status);
    }

    /**
     * Filtrar por rango de fechas.
     * Null-safe: Soporta startDate y/o endDate opcionales.
     * 
     * @param startDate Fecha inicio (inclusive), null = sin límite inferior
     * @param endDate   Fecha fin (inclusive), null = sin límite superior
     * @return Specification que filtra por rango de fechas
     */
    public static Specification<Borrow> byDateRange(Date startDate, Date endDate) {
        return (root, query, criteriaBuilder) -> {
            if (startDate == null && endDate == null)
                return null;

            if (startDate != null && endDate != null) {
                return criteriaBuilder.between(root.get("date"), startDate, endDate);
            }

            if (startDate != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("date"), startDate);
            }

            return criteriaBuilder.lessThanOrEqualTo(root.get("date"), endDate);
        };
    }

    /**
     * Filtrar por usuario que solicitó el préstamo.
     * Null-safe: Si userId es null, no aplica filtro.
     * 
     * @param userId ID del usuario
     * @return Specification que filtra por usuario
     */
    public static Specification<Borrow> byUser(Integer userId) {
        return (root, query, criteriaBuilder) -> userId == null ? null
                : criteriaBuilder.equal(root.get("usuario").get("id"), userId);
    }

    /**
     * Filtrar por administrador que procesó el préstamo.
     * Null-safe: Si adminId es null, no aplica filtro.
     * 
     * @param adminId ID del administrador
     * @return Specification que filtra por admin
     */
    public static Specification<Borrow> byAdmin(Integer adminId) {
        return (root, query, criteriaBuilder) -> adminId == null ? null
                : criteriaBuilder.equal(root.get("admin").get("id"), adminId);
    }

    /**
     * Fetch join para cargar detalles del préstamo (EVITA N+1).
     * Carga EAGER los details con sus materiales y subcategorías.
     * 
     * IMPORTANTE: Usar SOLO en findAll/findOne, NO en count queries.
     * 
     * @return Specification con fetch join de details
     */
    public static Specification<Borrow> withDetails() {
        return (root, query, criteriaBuilder) -> {
            // DISTINCT para evitar duplicados por join
            query.distinct(true);

            // Fetch details -> materials -> subCategory (3 niveles)
            Fetch<Object, Object> detailsFetch = root.fetch("details", JoinType.LEFT);
            Fetch<Object, Object> materialsFetch = detailsFetch.fetch("materials", JoinType.LEFT);
            materialsFetch.fetch("subCategory", JoinType.LEFT);

            return null; // Sin filtro, solo fetch
        };
    }

    /**
     * Fetch join para cargar usuario y admin (EVITA N+1).
     * Carga EAGER las relaciones de usuario y administrador.
     * 
     * @return Specification con fetch join de usuario y admin
     */
    public static Specification<Borrow> withUserAndAdmin() {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);
            root.fetch("usuario", JoinType.LEFT);
            root.fetch("admin", JoinType.LEFT);
            return null;
        };
    }

    /**
     * Combinación completa de fetch joins para cargar TODO en 1 query.
     * Equivalente a `findAll()` del repository (ahora sobrescrito para usar JOIN FETCH).
     * 
     * PERFORMANCE: 1 query vs N+1
     * 
     * @return Specification con todos los fetch joins
     */
    public static Specification<Borrow> withAllRelations() {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);

            // Fetch details -> materials -> subCategory
            Fetch<Object, Object> detailsFetch = root.fetch("details", JoinType.LEFT);
            Fetch<Object, Object> materialsFetch = detailsFetch.fetch("materials", JoinType.LEFT);
            materialsFetch.fetch("subCategory", JoinType.LEFT);

            // Fetch usuario y admin
            root.fetch("usuario", JoinType.LEFT);
            root.fetch("admin", JoinType.LEFT);

            return null;
        };
    }

    /**
     * EJEMPLO DE USO: Método auxiliar para queries comunes.
     * Reemplaza findByFiltersOptimized() con Specification composable.
     * 
     * @param usuarioId ID usuario (optional)
     * @param status    Status (optional)
     * @param startDate Fecha inicio (optional)
     * @param endDate   Fecha fin (optional)
     * @return Specification compuesta con todos los filtros
     */
    public static Specification<Borrow> byFilters(
            Integer usuarioId,
            Status status,
            Date startDate,
            Date endDate) {
        return Specification
                .where(byUser(usuarioId))
                .and(byStatus(status))
                .and(byDateRange(startDate, endDate))
                .and(withAllRelations()); // Incluye fetch joins
    }
}







