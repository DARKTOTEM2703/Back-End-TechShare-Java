package com.techmate.techmate.service.subcategories.manager;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.techmate.techmate.dto.SubCategoriesDTO;
import com.techmate.techmate.entity.Categories;
import com.techmate.techmate.entity.SubCategories;
import com.techmate.techmate.imageStorage.ImageStorageStrategy;
import com.techmate.techmate.repository.CategoriesRepository;
import com.techmate.techmate.repository.SubCategoriesRepository;
import com.techmate.techmate.service.CategoriesService;
import com.techmate.techmate.validation.ImageValidationStrategy;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

/**
 * 🎯 Gestor de operaciones CRUD y lógica de negocio para SubCategories (SRP).
 * 
 * PRINCIPIOS SOLID APLICADOS:
 * - SRP: Solo se encarga de CREATE, UPDATE, DELETE y lógica de SubCategories
 * - OCP: Extensible para nuevas operaciones sin modificar código existente
 * - LSP: Puede ser sustituido por cualquier implementación del contrato
 * - ISP: Interfaz específica para gestión de ciclo de vida
 * - DIP: Depende de abstracciones (Repository, Service) no de implementaciones
 * 
 * @author TechShare Team - SOLID Implementation
 */
@Slf4j
@Component
public class SubCategoriesManager {

    private final SubCategoriesRepository subCategoriesRepository;
    private final CategoriesRepository categoriesRepository;
    private final ImageStorageStrategy imageStorageStrategy;
    private final ImageValidationStrategy imageValidationStrategy;
    private final CategoriesService categoriesService;

    /**
     * Constructor injection para cumplir con DIP.
     */
    public SubCategoriesManager(
            SubCategoriesRepository subCategoriesRepository,
            CategoriesRepository categoriesRepository,
            ImageStorageStrategy imageStorageStrategy,
            ImageValidationStrategy imageValidationStrategy,
            CategoriesService categoriesService) {
        this.subCategoriesRepository = subCategoriesRepository;
        this.categoriesRepository = categoriesRepository;
        this.imageStorageStrategy = imageStorageStrategy;
        this.imageValidationStrategy = imageValidationStrategy;
        this.categoriesService = categoriesService;
    }

    // ==================== OPERACIÓN: CREATE ====================

    /**
     * Crea una nueva subcategoría en el sistema.
     */
    @Transactional
    public SubCategoriesDTO createSubCategory(SubCategoriesDTO subCategoryDTO, MultipartFile image) {
        log.info("Iniciando creación de subcategoría: {}", subCategoryDTO.getName());

        // 1. Validar nombre único
        if (subCategoriesRepository.findByName(subCategoryDTO.getName()) != null) {
            throw new IllegalArgumentException("Ya existe una subCategoria con el nombre: " + subCategoryDTO.getName());
        }

        // 2. Procesar imagen si se proporciona
        if (image != null && !image.isEmpty()) {
            processImage(subCategoryDTO, image);
        }

        // 3. Convertir y persistir
        SubCategories subCategory = convertToEntity(subCategoryDTO);
        SubCategories savedSubCategory = subCategoriesRepository.save(subCategory);

        log.info("Subcategoría creada exitosamente con ID: {}", savedSubCategory.getSubCategoryId());

        return convertToDTO(savedSubCategory);
    }

    // ==================== OPERACIÓN: UPDATE ====================

    /**
     * Actualiza una subcategoría existente.
     */
    @Transactional
    public SubCategoriesDTO updateSubCategory(int subCategoryID, SubCategoriesDTO subCategoryDTO, MultipartFile image) {
        log.info("Iniciando actualización de subcategoría ID: {}", subCategoryID);

        // 1. Encontrar subcategoría
        SubCategories subCategory = subCategoriesRepository.findById(subCategoryID)
                .orElseThrow(() -> new EntityNotFoundException("Subcategory not found with id: " + subCategoryID));

        // 2. Validar nombre único si cambió
        if (subCategoryDTO.getName() != null &&
                !subCategoryDTO.getName().equals(subCategory.getName()) &&
                subCategoriesRepository.findByName(subCategoryDTO.getName()) != null) {
            throw new IllegalArgumentException("Ya existe una subCategoria con el nombre: " + subCategoryDTO.getName());
        }

        // 3. Actualizar nombre
        if (subCategoryDTO.getName() != null) {
            subCategory.setName(subCategoryDTO.getName());
        }

        // 4. Actualizar categoría si se proporciona
        if (subCategoryDTO.getId() > 0) {
            Categories category = categoriesRepository.findById(subCategoryDTO.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con ID: " + subCategoryDTO.getId()));
            subCategory.setCategory(category);
        }

        // 5. Procesar imagen si se proporciona
        if (image != null && !image.isEmpty()) {
            updateImage(subCategory, image);
        }

        // 6. Persistir
        SubCategories updatedSubCategory = subCategoriesRepository.save(subCategory);

        log.info("Subcategoría actualizada exitosamente ID: {}", subCategoryID);

        return convertToDTO(updatedSubCategory);
    }

    // ==================== OPERACIÓN: DELETE ====================

    /**
     * Elimina una subcategoría del sistema.
     */
    @Transactional
    public void deleteSubCategory(int subCategoryID) {
        log.info("Iniciando eliminación de subcategoría ID: {}", subCategoryID);

        // 1. Encontrar subcategoría
        SubCategories subCategory = subCategoriesRepository.findById(subCategoryID)
                .orElseThrow(() -> new EntityNotFoundException("Subcategory not found with id: " + subCategoryID));

        // 2. Intentar eliminar imagen (best-effort)
        String imagePath = subCategory.getImagePath();
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                imageStorageStrategy.deleteImage(imagePath);
                log.debug("Imagen eliminada para subcategoría ID: {}", subCategoryID);
            } catch (Exception ex) {
                log.warn("No se pudo eliminar imagen para subcategoría ID: {}", subCategoryID, ex);
            }
        }

        // 3. Eliminar de base de datos
        subCategoriesRepository.deleteById(subCategoryID);

        log.info("Subcategoría eliminada exitosamente ID: {}", subCategoryID);
    }

    // ==================== HELPERS ====================

    /**
     * Procesa y almacena imagen de subcategoría.
     */
    private void processImage(SubCategoriesDTO dto, MultipartFile image) {
        imageValidationStrategy.validate(image);
        String savedImagePath = imageStorageStrategy.saveImage(image);
        dto.setImagePath(savedImagePath);
    }

    /**
     * Actualiza imagen de subcategoría existente.
     */
    private void updateImage(SubCategories subCategory, MultipartFile image) {
        imageValidationStrategy.validate(image);
        
        // Eliminar imagen antigua si existe
        String oldImagePath = subCategory.getImagePath();
        if (oldImagePath != null && !oldImagePath.isEmpty()) {
            try {
                imageStorageStrategy.deleteImage(oldImagePath);
                log.debug("Imagen antigua eliminada para subcategoría ID: {}", subCategory.getSubCategoryId());
            } catch (Exception ex) {
                log.warn("No se pudo eliminar imagen antigua para subcategoría ID: {}", subCategory.getSubCategoryId(), ex);
            }
        }

        // Guardar nueva imagen
        String newImagePath = imageStorageStrategy.saveImage(image);
        subCategory.setImagePath(newImagePath);
    }

    /**
     * Convierte SubCategoriesDTO a SubCategories.
     */
    private SubCategories convertToEntity(SubCategoriesDTO subCategoryDTO) {
        SubCategories subCategory = new SubCategories();
        subCategory.setName(subCategoryDTO.getName());
        subCategory.setImagePath(subCategoryDTO.getImagePath());

        // Buscar la categoría por ID y asignarla
        Categories category = categoriesRepository.findById(subCategoryDTO.getId())
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con ID: " + subCategoryDTO.getId()));
        subCategory.setCategory(category);

        return subCategory;
    }

    /**
     * Convierte SubCategories a SubCategoriesDTO.
     */
    private SubCategoriesDTO convertToDTO(SubCategories subCategory) {
        SubCategoriesDTO dto = new SubCategoriesDTO();
        dto.setId(subCategory.getSubCategoryId());
        dto.setName(subCategory.getName());
        dto.setImagePath(subCategory.getImagePath());
        
        if (subCategory.getCategory() != null) {
            dto.setId(subCategory.getCategory().getId());
            dto.setCategoryName(categoriesService.getCategoryNameById(subCategory.getCategory().getId()));
        }
        
        return dto;
    }
}
