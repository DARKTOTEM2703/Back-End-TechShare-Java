package com.techmate.techmate.domain.port.in;

import com.techmate.techmate.domain.model.material.Material;

/**
 * 🎯 INPUT PORT - UpdateMaterialUseCase
 * 
 * Caso de uso para actualizar materiales existentes.
 * 
 * @author TechShare Team - Hexagonal Architecture
 * @version 2.0.0
 */
public interface UpdateMaterialUseCase {

    /**
     * Actualiza un material existente
     * 
     * @param id ID del material a actualizar
     * @param request Datos actualizados
     * @return Material actualizado
     * @throws IllegalArgumentException si el material no existe
     */
    Material updateMaterial(Integer id, UpdateMaterialRequest request);

    /**
     * Aumenta el stock de un material (compra)
     */
    Material increaseStock(Integer id, int quantity);

    /**
     * Reduce el stock prestable (préstamo)
     */
    Material reduceBorrowableStock(Integer id, int quantity);

    /**
     * Restaura el stock prestable (devolución)
     */
    Material restoreBorrowableStock(Integer id, int quantity);

    /**
     * Elimina un material
     */
    void deleteMaterial(Integer id);

    /**
     * DTO para la actualización de materiales
     */
    record UpdateMaterialRequest(
        String name,
        String description,
        java.math.BigDecimal price,
        int stock,
        int borrowableStock,
        String imagePath,
        Integer subCategoryId
    ) {
        public UpdateMaterialRequest {
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
