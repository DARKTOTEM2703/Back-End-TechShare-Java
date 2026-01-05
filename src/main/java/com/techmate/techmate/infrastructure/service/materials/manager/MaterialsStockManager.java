package com.techmate.techmate.infrastructure.service.materials.manager;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.techmate.techmate.infrastructure.persistence.entity.Materials;
import com.techmate.techmate.infrastructure.exception.InsufficientStockException;
import com.techmate.techmate.infrastructure.exception.NotFoundException;
import com.techmate.techmate.infrastructure.persistence.repository.MaterialsRepository;

/**
 * Gestor de stock para Materials (SRP).
 */
@Component
public class MaterialsStockManager {

    private final MaterialsRepository materialsRepository;

    public MaterialsStockManager(MaterialsRepository materialsRepository) {
        this.materialsRepository = materialsRepository;
    }

    public int getAvailableStock(Integer materialId) {
        Materials m = materialsRepository.findById(materialId)
                .orElseThrow(() -> new NotFoundException("Material no encontrado con ID: " + materialId));
        return m.getBorrowableStock();
    }

    @Transactional
    public void reduceStock(Integer materialId, int quantity) {
        Materials m = materialsRepository.findById(materialId)
                .orElseThrow(() -> new NotFoundException("Material no encontrado con ID: " + materialId));
        if (m.getBorrowableStock() < quantity) {
            throw new InsufficientStockException("Stock insuficiente para material ID: " + materialId);
        }
        m.setBorrowableStock(m.getBorrowableStock() - quantity);
        materialsRepository.save(m);
    }

    @Transactional
    public void restoreStock(Integer materialId, int quantity) {
        Materials m = materialsRepository.findById(materialId)
                .orElseThrow(() -> new NotFoundException("Material no encontrado con ID: " + materialId));
        m.setBorrowableStock(m.getBorrowableStock() + quantity);
        materialsRepository.save(m);
    }
}







