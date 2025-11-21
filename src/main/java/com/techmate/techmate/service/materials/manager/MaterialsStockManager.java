package com.techmate.techmate.service.materials.manager;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.techmate.techmate.entity.Materials;
import com.techmate.techmate.exception.InsufficientStockException;
import com.techmate.techmate.exception.NotFoundException;
import com.techmate.techmate.repository.MaterialsRepository;

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
        return m.getBorrowable_stock();
    }

    @Transactional
    public void reduceStock(Integer materialId, int quantity) {
    Materials m = materialsRepository.findById(materialId)
        .orElseThrow(() -> new NotFoundException("Material no encontrado con ID: " + materialId));
    if (m.getBorrowable_stock() < quantity) {
        throw new InsufficientStockException("Stock insuficiente para material ID: " + materialId);
    }
        m.setBorrowable_stock(m.getBorrowable_stock() - quantity);
        materialsRepository.save(m);
    }

    @Transactional
    public void restoreStock(Integer materialId, int quantity) {
    Materials m = materialsRepository.findById(materialId)
        .orElseThrow(() -> new NotFoundException("Material no encontrado con ID: " + materialId));
        m.setBorrowable_stock(m.getBorrowable_stock() + quantity);
        materialsRepository.save(m);
    }
}


