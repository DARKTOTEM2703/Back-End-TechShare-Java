package com.techmate.techmate.domain.port.out;

import com.techmate.techmate.domain.model.material.SubCategory;

import java.util.List;
import java.util.Optional;

/**
 * 🔌 OUTPUT PORT - SubCategoryRepositoryPort
 * 
 * Define el contrato para persistencia de subcategorías.
 * 
 * @author TechShare Team - Hexagonal Architecture
 * @version 2.0.0
 */
public interface SubCategoryRepositoryPort {

    /**
     * Guarda una nueva subcategoría o actualiza una existente
     */
    SubCategory save(SubCategory subCategory);

    /**
     * Busca una subcategoría por su ID
     */
    Optional<SubCategory> findById(Integer id);

    /**
     * Obtiene todas las subcategorías
     */
    List<SubCategory> findAll();

    /**
     * Obtiene subcategorías por categoría padre
     */
    List<SubCategory> findByCategoryId(Integer categoryId);

    /**
     * Busca una subcategoría por nombre
     */
    Optional<SubCategory> findByName(String name);

    /**
     * Elimina una subcategoría por su ID
     */
    void deleteById(Integer id);

    /**
     * Verifica si existe una subcategoría por su ID
     */
    boolean existsById(Integer id);

    /**
     * Verifica si existe una subcategoría con el nombre dado en una categoría específica
     * (útil para validar duplicados dentro de una categoría)
     */
    boolean existsByNameAndCategoryId(String name, Integer categoryId);
}
