package com.techmate.techmate.domain.port.in;

import com.techmate.techmate.domain.model.material.Category;

import java.util.List;
import java.util.Optional;

/**
 * 🎯 INPUT PORT - ManageCategoriesUseCase
 * 
 * Caso de uso para gestión de categorías.
 * 
 * @author TechShare Team - Hexagonal Architecture
 * @version 2.0.0
 */
public interface ManageCategoriesUseCase {

    // ═══ QUERIES ═══
    
    List<Category> getAllCategories();
    
    Optional<Category> getCategoryById(Integer id);
    
    Optional<Category> getCategoryByName(String name);

    // ═══ COMMANDS ═══
    
    Category createCategory(CreateCategoryRequest request);
    
    Category updateCategory(Integer id, UpdateCategoryRequest request);
    
    void deleteCategory(Integer id);

    // ═══ DTOs ═══
    
    record CreateCategoryRequest(
        String name,
        String imagePath
    ) {
        public CreateCategoryRequest {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre de la categoría es obligatorio");
            }
        }
    }

    record UpdateCategoryRequest(
        String name,
        String imagePath
    ) {
        public UpdateCategoryRequest {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre de la categoría es obligatorio");
            }
        }
    }
}
