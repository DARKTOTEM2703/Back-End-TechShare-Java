package com.techmate.techmate.domain.port.in;

import com.techmate.techmate.domain.model.material.SubCategory;

import java.util.List;
import java.util.Optional;

/**
 * 🎯 INPUT PORT - ManageSubCategoriesUseCase
 * 
 * Caso de uso para gestión de subcategorías.
 * 
 * @author TechShare Team - Hexagonal Architecture
 * @version 2.0.0
 */
public interface ManageSubCategoriesUseCase {

    // ═══ QUERIES ═══
    
    List<SubCategory> getAllSubCategories();
    
    Optional<SubCategory> getSubCategoryById(Integer id);
    
    List<SubCategory> getSubCategoriesByCategoryId(Integer categoryId);
    
    Optional<SubCategory> getSubCategoryByName(String name);

    // ═══ COMMANDS ═══
    
    SubCategory createSubCategory(CreateSubCategoryRequest request);
    
    SubCategory updateSubCategory(Integer id, UpdateSubCategoryRequest request);
    
    void deleteSubCategory(Integer id);

    // ═══ DTOs ═══
    
    record CreateSubCategoryRequest(
        String name,
        String imagePath,
        Integer categoryId
    ) {
        public CreateSubCategoryRequest {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre de la subcategoría es obligatorio");
            }
            if (categoryId == null || categoryId <= 0) {
                throw new IllegalArgumentException("La categoría padre es obligatoria");
            }
        }
    }

    record UpdateSubCategoryRequest(
        String name,
        String imagePath,
        Integer categoryId
    ) {
        public UpdateSubCategoryRequest {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre de la subcategoría es obligatorio");
            }
            if (categoryId == null || categoryId <= 0) {
                throw new IllegalArgumentException("La categoría padre es obligatoria");
            }
        }
    }
}
