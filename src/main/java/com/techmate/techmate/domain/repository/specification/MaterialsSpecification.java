package com.techmate.techmate.domain.repository.specification;

import com.techmate.techmate.infrastructure.persistence.entity.Materials;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.*;

/**
 * Specification Pattern para Materials - Queries type-safe y composables.
 * 
 * VENTAJAS:
 * - Type-safe: El compilador valida las queries
 * - Composable: Combinar filtros dinámicamente
 * - DRY: Criterios reutilizables
 * - Testeable: Mock specifications fácilmente
 * - Búsqueda flexible: LIKE, IN, comparaciones
 * 
 * USAGE:
 * 
 * <pre>
 * List<Materials> results = materialsRepository.findAll(
 *         Specification.where(MaterialsSpecification.byAvailability(true))
 *                 .and(MaterialsSpecification.byCategory(categoryId))
 *                 .and(MaterialsSpecification.byNameLike("laptop")));
 * </pre>
 * 
 * @author TechMate Team
 * @since ITERACIÓN 3B
 */
public class MaterialsSpecification {

    /**
     * Filtrar por categoría.
     * Null-safe: Si categoryId es null, no aplica filtro.
     * 
     * @param categoryId ID de la categoría
     * @return Specification que filtra por categoría
     */
    public static Specification<Materials> byCategory(Integer categoryId) {
        return (root, query, criteriaBuilder) -> {
            if (categoryId == null)
                return null;
            return criteriaBuilder.equal(
                    root.get("subCategory").get("category").get("id"),
                    categoryId);
        };
    }

    /**
     * Filtrar por subcategoría.
     * Null-safe: Si subCategoryId es null, no aplica filtro.
     * 
     * @param subCategoryId ID de la subcategoría
     * @return Specification que filtra por subcategoría
     */
    public static Specification<Materials> bySubCategory(Integer subCategoryId) {
        return (root, query, criteriaBuilder) -> subCategoryId == null ? null
                : criteriaBuilder.equal(root.get("subCategory").get("id"), subCategoryId);
    }

    /**
     * Filtrar por disponibilidad.
     * 
     * @param available true = disponible, false = no disponible
     * @return Specification que filtra por availability
     */
    public static Specification<Materials> byAvailability(Boolean available) {
        return (root, query, criteriaBuilder) -> available == null ? null
                : criteriaBuilder.equal(root.get("available"), available);
    }

    /**
     * Filtrar por si es prestable (borrowable).
     * 
     * @param borrowable true = prestable, false = no prestable
     * @return Specification que filtra por borrowable
     */
    public static Specification<Materials> byBorrowable(Boolean borrowable) {
        return (root, query, criteriaBuilder) -> borrowable == null ? null
                : criteriaBuilder.equal(root.get("borrowable"), borrowable);
    }

    /**
     * Búsqueda por nombre (LIKE case-insensitive).
     * Null-safe: Si name es null o empty, no aplica filtro.
     * 
     * PERFORMANCE: Usa LOWER() para case-insensitive
     * 
     * @param name Texto a buscar en el nombre
     * @return Specification que busca por nombre
     */
    public static Specification<Materials> byNameLike(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null || name.trim().isEmpty())
                return null;

            String pattern = "%" + name.toLowerCase() + "%";
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")),
                    pattern);
        };
    }

    /**
     * Búsqueda por descripción (LIKE case-insensitive).
     * Null-safe: Si description es null o empty, no aplica filtro.
     * 
     * @param description Texto a buscar en la descripción
     * @return Specification que busca por descripción
     */
    public static Specification<Materials> byDescriptionLike(String description) {
        return (root, query, criteriaBuilder) -> {
            if (description == null || description.trim().isEmpty())
                return null;

            String pattern = "%" + description.toLowerCase() + "%";
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("description")),
                    pattern);
        };
    }

    /**
     * Fetch join para cargar subcategoría y categoría (EVITA N+1).
     * Carga EAGER las relaciones en 1 sola query.
     * 
     * IMPORTANTE: Usar SOLO en findAll/findOne, NO en count queries.
     * 
     * @return Specification con fetch join de subCategory
     */
    public static Specification<Materials> withSubCategory() {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);

            // Fetch subCategory -> category (2 niveles)
            Fetch<Object, Object> subCategoryFetch = root.fetch("subCategory", JoinType.LEFT);
            subCategoryFetch.fetch("category", JoinType.LEFT);

            return null; // Sin filtro, solo fetch
        };
    }

    /**
     * Filtrar materiales disponibles y prestables.
     * Combinación común: available=true AND borrowable=true
     * 
     * @return Specification para materiales disponibles y prestables
     */
    public static Specification<Materials> availableAndBorrowable() {
        return Specification
                .where(byAvailability(true))
                .and(byBorrowable(true));
    }

    /**
     * EJEMPLO DE USO: Búsqueda de inventario con filtros múltiples.
     * 
     * @param categoryId    Categoría (optional)
     * @param subCategoryId Subcategoría (optional)
     * @param available     Disponibilidad (optional)
     * @param borrowable    Prestable (optional)
     * @param searchText    Texto para buscar en nombre/descripción (optional)
     * @return Specification compuesta con todos los filtros
     */
    public static Specification<Materials> byInventoryFilters(
            Integer categoryId,
            Integer subCategoryId,
            Boolean available,
            Boolean borrowable,
            String searchText) {
        Specification<Materials> spec = Specification
                .where(byCategory(categoryId))
                .and(bySubCategory(subCategoryId))
                .and(byAvailability(available))
                .and(byBorrowable(borrowable))
                .and(withSubCategory()); // Incluye fetch join

        // Búsqueda de texto: nombre OR descripción
        if (searchText != null && !searchText.trim().isEmpty()) {
            spec = spec.and(
                    Specification.where(byNameLike(searchText))
                            .or(byDescriptionLike(searchText)));
        }

        return spec;
    }

    /**
     * Búsqueda de texto completa (nombre OR descripción).
     * 
     * @param searchText Texto a buscar
     * @return Specification que busca en nombre y descripción
     */
    public static Specification<Materials> byTextSearch(String searchText) {
        return Specification
                .where(byNameLike(searchText))
                .or(byDescriptionLike(searchText));
    }
}







