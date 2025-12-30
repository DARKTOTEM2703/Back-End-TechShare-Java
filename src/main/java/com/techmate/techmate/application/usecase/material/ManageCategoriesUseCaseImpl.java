package com.techmate.techmate.application.usecase.material;

import com.techmate.techmate.domain.model.material.Category;
import com.techmate.techmate.domain.port.in.ManageCategoriesUseCase;
import com.techmate.techmate.domain.port.out.CategoryRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 🎯 USE CASE - ManageCategoriesUseCaseImpl
 * 
 * Implementa la gestión de categorías.
 * 
 * @author TechShare Team - Hexagonal Architecture
 * @version 2.0.0
 */
@Service
@Transactional
public class ManageCategoriesUseCaseImpl implements ManageCategoriesUseCase {

    private static final Logger log = LoggerFactory.getLogger(ManageCategoriesUseCaseImpl.class);

    private final CategoryRepositoryPort categoryRepository;

    public ManageCategoriesUseCaseImpl(CategoryRepositoryPort categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Category> getCategoryById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID de la categoría debe ser válido");
        }
        return categoryRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Category> getCategoryByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la categoría no puede estar vacío");
        }
        return categoryRepository.findByName(name);
    }

    @Override
    public Category createCategory(CreateCategoryRequest request) {
        log.info("📁 Creando nueva categoría: {}", request.name());

        // Validar duplicados
        if (categoryRepository.existsByName(request.name())) {
            throw new IllegalArgumentException(
                String.format("Ya existe una categoría con el nombre '%s'", request.name()));
        }

        Category category = Category.builder()
            .name(request.name())
            .imagePath(request.imagePath())
            .build();

        Category saved = categoryRepository.save(category);
        log.info("✅ Categoría creada con ID: {}", saved.getId());
        return saved;
    }

    @Override
    public Category updateCategory(Integer id, UpdateCategoryRequest request) {
        log.info("🔄 Actualizando categoría ID: {}", id);

        Category existingCategory = categoryRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                String.format("Categoría con ID %d no existe", id)));

        // Validar duplicados (si cambió el nombre)
        if (!existingCategory.getName().equals(request.name())) {
            if (categoryRepository.existsByName(request.name())) {
                throw new IllegalArgumentException(
                    String.format("Ya existe otra categoría con el nombre '%s'", request.name()));
            }
        }

        Category updatedCategory = Category.builder()
            .id(id)
            .name(request.name())
            .imagePath(request.imagePath())
            .build();

        Category saved = categoryRepository.save(updatedCategory);
        log.info("✅ Categoría actualizada: {}", id);
        return saved;
    }

    @Override
    public void deleteCategory(Integer id) {
        log.info("🗑️ Eliminando categoría ID: {}", id);

        if (!categoryRepository.existsById(id)) {
            throw new IllegalArgumentException(
                String.format("Categoría con ID %d no existe", id));
        }

        categoryRepository.deleteById(id);
        log.info("✅ Categoría eliminada: {}", id);
    }
}
