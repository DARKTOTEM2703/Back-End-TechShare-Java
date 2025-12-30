package com.techmate.techmate.infra.mapper;

import com.techmate.techmate.domain.model.material.Category;
import com.techmate.techmate.domain.model.material.Material;
import com.techmate.techmate.domain.model.material.SubCategory;
import com.techmate.techmate.hexagonal.domain.entity.Categories;
import com.techmate.techmate.hexagonal.domain.entity.Materials;
import com.techmate.techmate.hexagonal.domain.entity.SubCategories;
import org.springframework.stereotype.Component;

/**
 * 🔄 DOMAIN MAPPER - DomainMaterialMapper
 * 
 * Convierte entre Domain Models (Material, Category, SubCategory)
 * y JPA Entities (Materials, Categories, SubCategories).
 * 
 * PATRÓN: Mapper Pattern
 * - Aísla el dominio de la infraestructura
 * - Previene que las anotaciones JPA contaminen el dominio
 * - Permite evolucionar dominio y persistencia independientemente
 * 
 * NOTA: Usa conversión manual en lugar de MapStruct para máximo control
 * y evitar dependencias circulares en mapeos complejos.
 * 
 * @author TechShare Team - Hexagonal Architecture
 * @version 2.0.0
 */
@Component
public class DomainMaterialMapper {

    // ═══════════════════════════════════════════════════════════════════
    // MATERIAL MAPPINGS
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Domain Model → JPA Entity
     */
    public Materials toEntity(Material domain) {
        if (domain == null) {
            return null;
        }

        Materials entity = new Materials();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setPrice(domain.getPrice());
        entity.setStock(domain.getStock());
        entity.setBorrowableStock(domain.getBorrowableStock());
        entity.setImagePath(domain.getImagePath());

        // Relación con SubCategory (solo el ID)
        if (domain.getSubCategoryId() != null) {
            SubCategories subCategory = new SubCategories();
            subCategory.setId(domain.getSubCategoryId());
            entity.setSubCategory(subCategory);
        }

        return entity;
    }

    /**
     * JPA Entity → Domain Model
     */
    public Material toDomain(Materials entity) {
        if (entity == null) {
            return null;
        }

        return Material.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .stock(entity.getStock())
                .borrowableStock(entity.getBorrowableStock())
                .imagePath(entity.getImagePath())
                .subCategoryId(entity.getSubCategory() != null ? entity.getSubCategory().getId() : null)
                .build();
    }

    // ═══════════════════════════════════════════════════════════════════
    // CATEGORY MAPPINGS
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Domain Model → JPA Entity
     */
    public Categories toEntity(Category domain) {
        if (domain == null) {
            return null;
        }

        Categories entity = new Categories();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setImagePath(domain.getImagePath());
        return entity;
    }

    /**
     * JPA Entity → Domain Model
     */
    public Category toDomain(Categories entity) {
        if (entity == null) {
            return null;
        }

        return Category.builder()
                .id(entity.getId())
                .name(entity.getName())
                .imagePath(entity.getImagePath())
                .build();
    }

    // ═══════════════════════════════════════════════════════════════════
    // SUBCATEGORY MAPPINGS
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Domain Model → JPA Entity
     */
    public SubCategories toEntity(SubCategory domain) {
        if (domain == null) {
            return null;
        }

        SubCategories entity = new SubCategories();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setImagePath(domain.getImagePath());

        // Relación con Category (solo el ID)
        if (domain.getCategoryId() != null) {
            Categories category = new Categories();
            category.setId(domain.getCategoryId());
            entity.setCategory(category);
        }

        return entity;
    }

    /**
     * JPA Entity → Domain Model
     */
    public SubCategory toDomain(SubCategories entity) {
        if (entity == null) {
            return null;
        }

        return SubCategory.builder()
                .id(entity.getId())
                .name(entity.getName())
                .imagePath(entity.getImagePath())
                .categoryId(entity.getCategory() != null ? entity.getCategory().getId() : null)
                .build();
    }
}
