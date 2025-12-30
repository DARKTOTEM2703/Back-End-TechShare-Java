package com.techmate.techmate.application.usecase.material;

import com.techmate.techmate.domain.model.material.SubCategory;
import com.techmate.techmate.domain.port.in.ManageSubCategoriesUseCase;
import com.techmate.techmate.domain.port.out.CategoryRepositoryPort;
import com.techmate.techmate.domain.port.out.SubCategoryRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 🎯 USE CASE - ManageSubCategoriesUseCaseImpl
 * 
 * Implementa la gestión de subcategorías.
 * 
 * @author TechShare Team - Hexagonal Architecture
 * @version 2.0.0
 */
@Service
@Transactional
public class ManageSubCategoriesUseCaseImpl implements ManageSubCategoriesUseCase {

    private static final Logger log = LoggerFactory.getLogger(ManageSubCategoriesUseCaseImpl.class);

    private final SubCategoryRepositoryPort subCategoryRepository;
    private final CategoryRepositoryPort categoryRepository;

    public ManageSubCategoriesUseCaseImpl(
            SubCategoryRepositoryPort subCategoryRepository,
            CategoryRepositoryPort categoryRepository) {
        this.subCategoryRepository = subCategoryRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubCategory> getAllSubCategories() {
        return subCategoryRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SubCategory> getSubCategoryById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID de la subcategoría debe ser válido");
        }
        return subCategoryRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubCategory> getSubCategoriesByCategoryId(Integer categoryId) {
        if (categoryId == null || categoryId <= 0) {
            throw new IllegalArgumentException("El ID de la categoría debe ser válido");
        }
        return subCategoryRepository.findByCategoryId(categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SubCategory> getSubCategoryByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la subcategoría no puede estar vacío");
        }
        return subCategoryRepository.findByName(name);
    }

    @Override
    public SubCategory createSubCategory(CreateSubCategoryRequest request) {
        log.info("📂 Creando nueva subcategoría: {} en categoría ID: {}", 
            request.name(), request.categoryId());

        // Validar que la categoría padre existe
        if (!categoryRepository.existsById(request.categoryId())) {
            throw new IllegalArgumentException(
                String.format("La categoría con ID %d no existe", request.categoryId()));
        }

        // Validar duplicados dentro de la misma categoría
        if (subCategoryRepository.existsByNameAndCategoryId(request.name(), request.categoryId())) {
            throw new IllegalArgumentException(
                String.format("Ya existe una subcategoría '%s' en esta categoría", request.name()));
        }

        SubCategory subCategory = SubCategory.builder()
            .name(request.name())
            .imagePath(request.imagePath())
            .categoryId(request.categoryId())
            .build();

        SubCategory saved = subCategoryRepository.save(subCategory);
        log.info("✅ Subcategoría creada con ID: {}", saved.getId());
        return saved;
    }

    @Override
    public SubCategory updateSubCategory(Integer id, UpdateSubCategoryRequest request) {
        log.info("🔄 Actualizando subcategoría ID: {}", id);

        SubCategory existing = subCategoryRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                String.format("Subcategoría con ID %d no existe", id)));

        // Validar que la nueva categoría padre existe
        if (!categoryRepository.existsById(request.categoryId())) {
            throw new IllegalArgumentException(
                String.format("La categoría con ID %d no existe", request.categoryId()));
        }

        // Validar duplicados (si cambió el nombre o la categoría)
        if (!existing.getName().equals(request.name()) || 
            !existing.getCategoryId().equals(request.categoryId())) {
            if (subCategoryRepository.existsByNameAndCategoryId(request.name(), request.categoryId())) {
                throw new IllegalArgumentException(
                    String.format("Ya existe una subcategoría '%s' en esta categoría", request.name()));
            }
        }

        SubCategory updated = SubCategory.builder()
            .id(id)
            .name(request.name())
            .imagePath(request.imagePath())
            .categoryId(request.categoryId())
            .build();

        SubCategory saved = subCategoryRepository.save(updated);
        log.info("✅ Subcategoría actualizada: {}", id);
        return saved;
    }

    @Override
    public void deleteSubCategory(Integer id) {
        log.info("🗑️ Eliminando subcategoría ID: {}", id);

        if (!subCategoryRepository.existsById(id)) {
            throw new IllegalArgumentException(
                String.format("Subcategoría con ID %d no existe", id));
        }

        subCategoryRepository.deleteById(id);
        log.info("✅ Subcategoría eliminada: {}", id);
    }
}
