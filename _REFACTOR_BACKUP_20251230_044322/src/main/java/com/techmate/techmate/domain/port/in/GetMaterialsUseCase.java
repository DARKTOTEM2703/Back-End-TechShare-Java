package com.techmate.techmate.domain.port.in;

import com.techmate.techmate.domain.model.material.Material;

import java.util.List;
import java.util.Optional;

/**
 * 🎯 INPUT PORT - GetMaterialsUseCase
 * 
 * Caso de uso para consultar materiales.
 * Define las operaciones de lectura del dominio.
 * 
 * @author TechShare Team - Hexagonal Architecture
 * @version 2.0.0
 */
public interface GetMaterialsUseCase {

    /**
     * Obtiene todos los materiales
     */
    List<Material> getAllMaterials();

    /**
     * Busca un material por su ID
     */
    Optional<Material> getMaterialById(Integer id);

    /**
     * Obtiene materiales por subcategoría
     */
    List<Material> getMaterialsBySubCategory(Integer subCategoryId);

    /**
     * Busca materiales por nombre (búsqueda parcial)
     */
    List<Material> searchMaterialsByName(String name);

    /**
     * Obtiene materiales disponibles para préstamo
     */
    List<Material> getAvailableMaterialsForBorrow();

    /**
     * Obtiene materiales paginados por subcategoría
     */
    List<Material> getMaterialsBySubCategoryPaginated(Integer subCategoryId, int page, int size);
}
