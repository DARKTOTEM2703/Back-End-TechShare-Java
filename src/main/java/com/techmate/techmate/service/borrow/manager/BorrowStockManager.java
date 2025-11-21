package com.techmate.techmate.service.borrow.manager;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.techmate.techmate.entity.Materials;
import com.techmate.techmate.exception.BorrowBusinessException;
import com.techmate.techmate.repository.MaterialsRepository;

/**
 * Gestor de stock especializado para operaciones de préstamo siguiendo SRP.
 * 
 * PRINCIPIOS SOLID APLICADOS:
 * - SRP: Solo se encarga de la gestión de stock para préstamos
 * - OCP: Extensible para nuevas reglas de stock sin modificar código existente
 * - LSP: Puede ser sustituido por cualquier implementación del contrato
 * - ISP: Interfaz específica para gestión de stock de préstamos
 * - DIP: Depende de abstracciones (Repository) no de implementaciones concretas
 * 
 * RESPONSABILIDADES:
 * - Validar disponibilidad de stock para préstamos
 * - Reducir stock cuando se aprueba un préstamo
 * - Restaurar stock cuando se devuelve un préstamo
 * - Manejar transacciones de stock de forma atómica
 * 
 * @author TechShare Team - SOLID Implementation
 */
@Component
public class BorrowStockManager implements IBorrowStockManager {
    
    private final MaterialsRepository materialsRepository;
    
    /**
     * Constructor injection para cumplir con DIP.
     * 
     * @param materialsRepository Repositorio de materiales
     */
    public BorrowStockManager(MaterialsRepository materialsRepository) {
        this.materialsRepository = materialsRepository;
    }
    
    /**
     * Valida si hay suficiente stock disponible para préstamo.
     * 
     * @param materialId ID del material
     * @param requestedQuantity Cantidad solicitada
     * @return true si hay suficiente stock
     * @throws RuntimeException si no hay stock suficiente o material no encontrado
     */
    public boolean validateStockAvailability(Integer materialId, int requestedQuantity) {

        Materials material = materialsRepository.findById(materialId)
                .orElseThrow(() -> BorrowBusinessException.materialNotFound(materialId));

        int availableStock = material.getBorrowable_stock();

        if (availableStock < requestedQuantity) {
            throw BorrowBusinessException.insufficientStock(materialId, requestedQuantity, availableStock);
        }

        return true;
    }
    
    /**
     * Reduce el stock disponible cuando se aprueba un préstamo.
     * Operación transaccional para mantener consistencia.
     * 
     * @param materialId ID del material
     * @param quantity Cantidad a reducir
     * @throws RuntimeException si no se puede reducir el stock
     */
    @Transactional
    public void reduceStock(Integer materialId, int quantity) {
        
        Materials material = materialsRepository.findById(materialId)
                .orElseThrow(() -> BorrowBusinessException.materialNotFound(materialId));

        // Validar stock disponible
        if (material.getBorrowable_stock() < quantity) {
            throw BorrowBusinessException.insufficientStock(materialId, quantity, material.getBorrowable_stock());
        }

        // Reducir stock
        material.setBorrowable_stock(material.getBorrowable_stock() - quantity);

        // Guardar cambios
        materialsRepository.save(material);
    }
    
    /**
     * Restaura el stock cuando se devuelve un préstamo.
     * Operación transaccional para mantener consistencia.
     * 
     * @param materialId ID del material
     * @param quantity Cantidad a restaurar
     * @throws RuntimeException si no se puede restaurar el stock
     */
    @Transactional
    public void restoreStock(Integer materialId, int quantity) {
        
    Materials material = materialsRepository.findById(materialId)
        .orElseThrow(() -> BorrowBusinessException.materialNotFound(materialId));

    // Restaurar stock
    material.setBorrowable_stock(material.getBorrowable_stock() + quantity);

    // Guardar cambios
    materialsRepository.save(material);
    }
    
    /**
     * Obtiene el stock disponible actual para un material.
     * 
     * @param materialId ID del material
     * @return Cantidad de stock disponible
     */
    public int getAvailableStock(Integer materialId) {
        
    Materials material = materialsRepository.findById(materialId)
        .orElseThrow(() -> BorrowBusinessException.materialNotFound(materialId));

    return material.getBorrowable_stock();
    }
    
    /**
     * Verifica si un material está disponible para préstamos.
     * 
     * @param materialId ID del material
     * @return true si el material está disponible para préstamos
     */
    public boolean isMaterialBorrowable(Integer materialId) {
        
    Materials material = materialsRepository.findById(materialId)
        .orElseThrow(() -> BorrowBusinessException.materialNotFound(materialId));

    // Un material es prestable si tiene stock > 0
    return material.getBorrowable_stock() > 0;
    }
}

