package com.techmate.techmate.service.categories.manager;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.techmate.techmate.dto.CategoriesDTO;
import com.techmate.techmate.entity.Categories;
import com.techmate.techmate.exception.BusinessException;
import com.techmate.techmate.imageStorage.ImageStorageStrategy;
import com.techmate.techmate.repository.CategoriesRepository;
import com.techmate.techmate.service.categories.mapper.CategoriesMapper;
import com.techmate.techmate.service.categories.validator.CategoriesValidator;
import com.techmate.techmate.validation.ImageValidationStrategy;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

/**
 * 🎯 Gestor de operaciones CRUD y lógica de negocio para Categories (SRP).
 * 
 * PRINCIPIOS SOLID APLICADOS:
 * - SRP: Solo se encarga de CREATE, UPDATE, DELETE y lógica de Categories
 * - OCP: Extensible para nuevas operaciones sin modificar código existente
 * - LSP: Puede ser sustituido por cualquier implementación del contrato
 * - ISP: Interfaz específica para gestión de ciclo de vida
 * - DIP: Depende de abstracciones (Repository, Mapper, Validator) no de implementaciones
 * 
 * RESPONSABILIDADES:
 * - Crear nuevas categorías con validación e imagen
 * - Actualizar categorías existentes (nombre, imagen)
 * - Eliminar categorías de la base de datos
 * - Procesar y almacenar imágenes
 * 
 * @author TechShare Team - SOLID Implementation
 */
@Slf4j
@Component
public class CategoriesManager {

    private final CategoriesRepository categoriesRepository;
    private final ImageStorageStrategy imageStorageStrategy;
    private final ImageValidationStrategy imageValidationStrategy;
    private final CategoriesMapper categoriesMapper;
    private final CategoriesValidator categoriesValidator;

    /**
     * Constructor injection para cumplir con DIP.
     */
    public CategoriesManager(
            CategoriesRepository categoriesRepository,
            ImageStorageStrategy imageStorageStrategy,
            ImageValidationStrategy imageValidationStrategy,
            CategoriesMapper categoriesMapper,
            CategoriesValidator categoriesValidator) {
        this.categoriesRepository = categoriesRepository;
        this.imageStorageStrategy = imageStorageStrategy;
        this.imageValidationStrategy = imageValidationStrategy;
        this.categoriesMapper = categoriesMapper;
        this.categoriesValidator = categoriesValidator;
    }

    // ==================== OPERACIÓN: CREATE ====================

    /**
     * Crea una nueva categoría en el sistema.
     * 
     * FLUJO:
     * 1. Validar que no exista otra categoría con el mismo nombre
     * 2. Validar y guardar imagen si se proporciona
     * 3. Convertir DTO a entidad
     * 4. Persistir en base de datos
     * 5. Retornar DTO de la categoría creada
     * 
     * @param categoriesDTO Datos de la categoría a crear
     * @param image         Archivo de imagen (opcional)
     * @return CategoriesDTO de la categoría creada (con ID asignado)
     * @throws IllegalArgumentException si ya existe categoría con ese nombre
     */
    @Transactional
    public CategoriesDTO createCategory(CategoriesDTO categoriesDTO, MultipartFile image) {
        log.info("Iniciando creación de categoría: {}", categoriesDTO.getName());

        // 1. Validar nombre único
        categoriesValidator.validateUniqueName(categoriesDTO.getName());

        // 2. Procesar imagen si se proporciona
        if (image != null && !image.isEmpty()) {
            processImage(categoriesDTO, image);
        }

        // 3. Convertir y persistir
        Categories categories = categoriesMapper.toEntity(categoriesDTO);
        Categories savedCategory = categoriesRepository.save(categories);

        log.info("Categoría creada exitosamente con ID: {}", savedCategory.getId());

        // 4. Retornar DTO
        return categoriesMapper.toDTO(savedCategory);
    }

    // ==================== OPERACIÓN: UPDATE ====================

    /**
     * Actualiza una categoría existente.
     * 
     * FLUJO:
     * 1. Encontrar categoría por ID
     * 2. Validar nombre único si cambió
     * 3. Actualizar nombre
     * 4. Procesar y actualizar imagen si se proporciona
     * 5. Persistir cambios
     * 6. Retornar DTO actualizado
     * 
     * @param categoryID    ID de la categoría a actualizar
     * @param categoriesDTO Datos con los nuevos valores
     * @param image         Nuevo archivo de imagen (opcional)
     * @return CategoriesDTO de la categoría actualizada
     * @throws EntityNotFoundException si la categoría no existe
     */
    @Transactional
    public CategoriesDTO updateCategory(int categoryID, CategoriesDTO categoriesDTO, MultipartFile image) {
        log.info("Iniciando actualización de categoría ID: {}", categoryID);

        // 1. Encontrar categoría
        Categories categories = categoriesRepository.findById(categoryID)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + categoryID));

        // 2. Validar nombre único si cambió
        if (categoriesDTO.getName() != null && !categoriesDTO.getName().equals(categories.getName())) {
            categoriesValidator.validateUniqueName(categoriesDTO.getName());
        }

        // 3. Actualizar nombre
        if (categoriesDTO.getName() != null && !categoriesDTO.getName().isEmpty()) {
            categories.setName(categoriesDTO.getName());
        }

        // 4. Procesar imagen si se proporciona
        if (image != null && !image.isEmpty()) {
            updateImage(categories, image);
        }

        // 5. Persistir
        Categories updatedCategory = categoriesRepository.save(categories);

        log.info("Categoría actualizada exitosamente ID: {}", categoryID);

        // 6. Retornar DTO
        return categoriesMapper.toDTO(updatedCategory);
    }

    // ==================== OPERACIÓN: DELETE ====================

    /**
     * Elimina una categoría del sistema.
     * 
     * FLUJO:
     * 1. Encontrar categoría por ID
     * 2. Eliminar imagen si existe (best-effort)
     * 3. Eliminar categoría de la base de datos
     * 
     * @param categoryID ID de la categoría a eliminar
     * @throws EntityNotFoundException si la categoría no existe
     */
    @Transactional
    public void deleteCategory(int categoryID) {
        log.info("Iniciando eliminación de categoría ID: {}", categoryID);

        // 1. Encontrar categoría
        Categories category = categoriesRepository.findById(categoryID)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + categoryID));

        // 2. Intentar eliminar imagen (best-effort)
        String imagePath = category.getImagePath();
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                imageStorageStrategy.deleteImage(imagePath);
                log.debug("Imagen eliminada para categoría ID: {}", categoryID);
            } catch (Exception ex) {
                log.warn("No se pudo eliminar imagen para categoría ID: {} - Continuando con eliminación lógica", 
                         categoryID, ex);
            }
        }

        // 3. Eliminar de base de datos
        categoriesRepository.deleteById(categoryID);

        log.info("Categoría eliminada exitosamente ID: {}", categoryID);
    }

    // ==================== HELPERS ====================

    /**
     * Procesa y almacena imagen de categoría.
     * 
     * @param dto   DTO a actualizar con ruta de imagen
     * @param image Archivo de imagen
     */
    private void processImage(CategoriesDTO dto, MultipartFile image) {
        imageValidationStrategy.validate(image);
        String savedImagePath = imageStorageStrategy.saveImage(image);
        dto.setImagePath(savedImagePath);
    }

    /**
     * Actualiza imagen de categoría existente.
     * 
     * @param category Entidad a actualizar
     * @param image    Nuevo archivo de imagen
     */
    private void updateImage(Categories category, MultipartFile image) {
        imageValidationStrategy.validate(image);
        
        // Eliminar imagen antigua si existe
        String oldImagePath = category.getImagePath();
        if (oldImagePath != null && !oldImagePath.isEmpty()) {
            try {
                imageStorageStrategy.deleteImage(oldImagePath);
                log.debug("Imagen antigua eliminada para categoría ID: {}", category.getId());
            } catch (Exception ex) {
                log.warn("No se pudo eliminar imagen antigua para categoría ID: {}", category.getId(), ex);
            }
        }

        // Guardar nueva imagen
        String newImagePath = imageStorageStrategy.saveImage(image);
        category.setImagePath(newImagePath);
    }
}
