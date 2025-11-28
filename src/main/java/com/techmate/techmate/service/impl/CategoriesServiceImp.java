package com.techmate.techmate.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.techmate.techmate.validation.ImageValidationStrategy;
import com.techmate.techmate.dto.CategoriesDTO;
import com.techmate.techmate.entity.Categories;
import com.techmate.techmate.imageStorage.ImageStorageStrategy;
import com.techmate.techmate.repository.CategoriesRepository;
import com.techmate.techmate.service.CategoriesService;
import com.techmate.techmate.service.categories.mapper.CategoriesMapper;
import com.techmate.techmate.service.categories.query.CategoriesQueryService;
import com.techmate.techmate.service.categories.validator.CategoriesValidator;

import jakarta.persistence.EntityNotFoundException;

/**
 * La clase {@code CategoriesServiceImp} es la implementación de la interfaz
 * {@code CategoriesService}. Proporciona métodos para manejar operaciones
 * relacionadas con las categorías en el sistema, incluyendo la creación,
 * recuperación, actualización y eliminación de categorías.
 * 
 * <p>
 * Esta clase utiliza {@code CategoriesRepository} para acceder a los
 * datos de las categorías en la base de datos y realiza la conversión entre
 * entidades y objetos de transferencia de datos (DTO).
 * </p>
 */
@Service
public class CategoriesServiceImp implements CategoriesService {

    private final CategoriesRepository categoriesRepository;
    private final ImageStorageStrategy imageStorageStrategy;
    private final ImageValidationStrategy imageValidationStrategy; // Inyección de la estrategia de validación
    private final CategoriesMapper categoriesMapper;
    private final CategoriesValidator categoriesValidator;
    private final CategoriesQueryService categoriesQueryService;

    @Value("${storage.location}")
    private String storageLocation; // Directorio para almacenar imágenes

    @Value("${server.url}")
    private String serverUrl; // URL base del servidor

    public CategoriesServiceImp(CategoriesRepository categoriesRepository,
                                ImageStorageStrategy imageStorageStrategy,
                                ImageValidationStrategy imageValidationStrategy,
                                CategoriesMapper categoriesMapper,
                                CategoriesValidator categoriesValidator,
                                CategoriesQueryService categoriesQueryService) {
        this.categoriesRepository = categoriesRepository;
        this.imageStorageStrategy = imageStorageStrategy;
        this.imageValidationStrategy = imageValidationStrategy;
        this.categoriesMapper = categoriesMapper;
        this.categoriesValidator = categoriesValidator;
        this.categoriesQueryService = categoriesQueryService;
    }

    /**
     * Convierte un objeto {@code CategoriesDTO} a una entidad {@code Categories}.
     * 
     * @param categoriesDTO El objeto DTO a convertir.
     * @return La entidad {@code Categories} correspondiente.
     */
    private Categories convertToEntity(CategoriesDTO categoriesDTO) {
        return categoriesMapper.toEntity(categoriesDTO);
    }

    @Override
    public CategoriesDTO createCategory(CategoriesDTO categoriesDTO, MultipartFile image) {
        // Verificar si ya existe una categoría con el mismo nombre
        categoriesValidator.validateUniqueName(categoriesDTO.getName());
    // Validar la imagen completa usando la estrategia (ahora acepta MultipartFile)
    imageValidationStrategy.validate(image);

        // Guardar la imagen y obtener la ruta
        String savedImagePath = imageStorageStrategy.saveImage(image); // Asegúrate de que este método acepte

        categoriesDTO.setImagePath(savedImagePath);

        // Establecer la ruta de la imagen en el DTO
        categoriesDTO.setImagePath(savedImagePath);

        // Convertir el DTO a entidad y guardarlo en la base de datos
    Categories categories = convertToEntity(categoriesDTO);
    categories = categoriesRepository.save(categories);
    return categoriesMapper.toDTO(categories);
    }

    @Override
    public CategoriesDTO getCategoryById(int categoryID) {
        // Buscar la categoría por ID y lanzar excepción si no se encuentra
        return categoriesQueryService.getById(categoryID);
    }

    @Override
    public CategoriesDTO updateCategory(int categoryID, CategoriesDTO categoriesDTO, MultipartFile image) {
        // Buscar la categoría por ID y lanzar excepción si no se encuentra
        Categories categories = categoriesRepository.findById(categoryID)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + categoryID));

        // Verificar nombre único si cambió
        if (categoriesDTO.getName() != null && !categoriesDTO.getName().equals(categories.getName())) {
            categoriesValidator.validateUniqueName(categoriesDTO.getName());
        }

        // Actualizar el nombre solo si es diferente y no es nulo
        if (categoriesDTO.getName() != null && !categoriesDTO.getName().isEmpty()) {
            categories.setName(categoriesDTO.getName());
        }

        // Si se proporciona una imagen, validar y guardar la nueva imagen
        if (image != null && !image.isEmpty()) {
            String oldImagePath = categories.getImagePath();

            // Eliminar la imagen antigua si existe
            if (oldImagePath != null && !oldImagePath.isEmpty()) {
                imageStorageStrategy.deleteImage(oldImagePath);
            }

            // Validar la nueva imagen usando la estrategia de validación
            imageValidationStrategy.validate(image);

            // Guardar la nueva imagen y establecer su ruta en la entidad
            String newImagePath = imageStorageStrategy.saveImage(image);
            categories.setImagePath(newImagePath); // Actualizar la ruta de la imagen
        }

        // Guardar la entidad actualizada en la base de datos
        categories = categoriesRepository.save(categories);
        return categoriesMapper.toDTO(categories); // Devolver la categoría actualizada
    }

    @Override
    public void deleteCategory(int categoryID) {
        // Buscar la categoría por ID
        Categories category = categoriesRepository.findById(categoryID)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + categoryID));

        // Si se encuentra la categoría, eliminar la imagen si existe
        String imagePath = category.getImagePath();
        if (imagePath != null && !imagePath.isEmpty()) {
            imageStorageStrategy.deleteImage(imagePath); // Utilizar la estrategia para eliminar la imagen
        }

        // Eliminar la categoría de la base de datos
        categoriesRepository.deleteById(categoryID);
    }

    @Override
    public List<CategoriesDTO> getAllCategories() {
        return categoriesQueryService.getAll();
    }

    @Override
    public String getCategoryNameById(int categoryId) {
        Categories category = categoriesRepository.findById(categoryId).orElse(null);
        return category != null ? category.getName() : null;
    }
}


