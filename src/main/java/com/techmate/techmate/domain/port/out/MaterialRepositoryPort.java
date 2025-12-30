package com.techmate.techmate.domain.port.out;

import com.techmate.techmate.domain.model.material.Material;

import java.util.List;
import java.util.Optional;

/**
 * 🔌 OUTPUT PORT - MaterialRepositoryPort
 * 
 * Define el contrato para persistencia de materiales.
 * Esta interfaz pertenece al dominio pero será implementada en la infraestructura.
 * 
 * PATRÓN: Dependency Inversion Principle (SOLID)
 * El dominio define la interfaz, la infraestructura la implementa.
 * 
 * @author TechShare Team - Hexagonal Architecture
 * @version 2.0.0
 */
public interface MaterialRepositoryPort {

    /**
     * Guarda un nuevo material o actualiza uno existente
     */
    Material save(Material material);

    /**
     * Busca un material por su ID
     * @return Optional con el material si existe, Optional.empty() si no existe
     */
    Optional<Material> findById(Integer id);

    /**
     * Obtiene todos los materiales
     */
    List<Material> findAll();

    /**
     * Obtiene materiales por subcategoría
     */
    List<Material> findBySubCategoryId(Integer subCategoryId);

    /**
     * Obtiene materiales por nombre (búsqueda parcial)
     */
    List<Material> findByNameContaining(String name);

    /**
     * Obtiene materiales con stock disponible para préstamo
     */
    List<Material> findAvailableForBorrow();

    /**
     * Elimina un material por su ID
     */
    void deleteById(Integer id);

    /**
     * Verifica si existe un material por su ID
     */
    boolean existsById(Integer id);

    /**
     * Obtiene el conteo total de materiales
     */
    long count();

    /**
     * Obtiene materiales paginados por subcategoría
     * @param subCategoryId ID de la subcategoría
     * @param page Número de página (0-indexed)
     * @param size Tamaño de página
     * @return Lista de materiales en la página solicitada
     */
    List<Material> findBySubCategoryIdPaginated(Integer subCategoryId, int page, int size);
}
