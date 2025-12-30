package com.techmate.techmate.hexagonal.infrastructure.service.subcategories.query;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import com.techmate.techmate.hexagonal.infrastructure.dto.SubCategoriesDTO;
import com.techmate.techmate.hexagonal.domain.entity.SubCategories;
import com.techmate.techmate.hexagonal.domain.repository.SubCategoriesRepository;
import com.techmate.techmate.hexagonal.infrastructure.service.CategoriesService;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

/**
 * 🎯 Servicio de consultas especializado para subcategorías (SRP).
 * 
 * RESPONSABILIDADES:
 * - Obtener todas las subcategorías
 * - Buscar subcategoría por ID
 * - Obtener nombre de subcategoría
 * 
 * @author TechShare Team - SOLID Implementation
 */
@Slf4j
@Component
public class SubCategoriesQueryService {

    private final SubCategoriesRepository subCategoriesRepository;
    private final CategoriesService categoriesService;

    /**
     * Constructor injection para cumplir con DIP.
     */
    public SubCategoriesQueryService(SubCategoriesRepository subCategoriesRepository,
                                     CategoriesService categoriesService) {
        this.subCategoriesRepository = subCategoriesRepository;
        this.categoriesService = categoriesService;
    }

    /**
     * Obtiene todas las subcategorías.
     */
    @Transactional(readOnly = true)
    public List<SubCategoriesDTO> getAll() {
        return subCategoriesRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene una subcategoría por su ID.
     */
    @Transactional(readOnly = true)
    public SubCategoriesDTO getById(int subCategoryID) {
        SubCategories subCategory = subCategoriesRepository.findById(subCategoryID)
                .orElseThrow(() -> new EntityNotFoundException("Subcategory not found with id: " + subCategoryID));
        return convertToDTO(subCategory);
    }

    /**
     * Obtiene el nombre de una subcategoría por su ID.
     */
    @Transactional(readOnly = true)
    public String getNameById(int subCategoryID) {
        SubCategories subCategory = subCategoriesRepository.findById(subCategoryID).orElse(null);
        return subCategory != null ? subCategory.getName() : null;
    }

    /**
     * Convierte SubCategories a SubCategoriesDTO.
     * Nota: Incluye lógica para resolver categoryName desde CategoriesService.
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







