package com.techmate.techmate.domain.port.in;

import com.techmate.techmate.domain.model.material.Material;

/**
 * 🎯 INPUT PORT - CreateMaterialUseCase
 * 
 * Caso de uso para crear nuevos materiales.
 * 
 * @author TechShare Team - Hexagonal Architecture
 * @version 2.0.0
 */
public interface CreateMaterialUseCase {

    /**
     * Crea un nuevo material en el sistema
     * 
     * @param request Datos del material a crear
     * @return Material creado con su ID asignado
     * @throws IllegalArgumentException si la subcategoría no existe
     */
    Material createMaterial(CreateMaterialRequest request);

    /**
     * DTO para la creación de materiales
     */
    record CreateMaterialRequest(
        String name,
        String description,
        java.math.BigDecimal price,
        int stock,
        int borrowableStock,
        String imagePath,
        Integer subCategoryId
    ) {
        public CreateMaterialRequest {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre del material es obligatorio");
            }
            if (description == null || description.trim().isEmpty()) {
                throw new IllegalArgumentException("La descripción del material es obligatoria");
            }
            if (price == null || price.compareTo(java.math.BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("El precio debe ser mayor que 0");
            }
            if (stock < 0) {
                throw new IllegalArgumentException("El stock no puede ser negativo");
            }
            if (borrowableStock < 0) {
                throw new IllegalArgumentException("El stock prestable no puede ser negativo");
            }
            if (borrowableStock > stock) {
                throw new IllegalArgumentException("El stock prestable no puede exceder el stock total");
            }
            if (subCategoryId == null || subCategoryId <= 0) {
                throw new IllegalArgumentException("La subcategoría es obligatoria");
            }
        }
    }
}
