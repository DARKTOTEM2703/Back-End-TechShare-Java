package com.techmate.techmate.domain.port.out;

import com.techmate.techmate.domain.model.material.Category;

import java.util.List;
import java.util.Optional;

/**
 * 🔌 OUTPUT PORT - CategoryRepositoryPort
 * 
 * Define el contrato para persistencia de categorías.
 * 
 * @author TechShare Team - Hexagonal Architecture
 * @version 2.0.0
 */
public interface CategoryRepositoryPort {

    /**
     * Guarda una nueva categoría o actualiza una existente
     */
    Category save(Category category);

    /**
     * Busca una categoría por su ID
     */
    Optional<Category> findById(Integer id);

    /**
     * Obtiene todas las categorías
     */
    List<Category> findAll();

    /**
     * Busca una categoría por nombre
     */
    Optional<Category> findByName(String name);

    /**
     * Elimina una categoría por su ID
     */
    void deleteById(Integer id);

    /**
     * Verifica si existe una categoría por su ID
     */
    boolean existsById(Integer id);

    /**
     * Verifica si existe una categoría con el nombre dado
     * (útil para validar duplicados)
     */
    boolean existsByName(String name);
}
