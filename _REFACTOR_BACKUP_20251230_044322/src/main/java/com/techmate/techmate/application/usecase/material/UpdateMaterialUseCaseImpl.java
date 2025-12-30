package com.techmate.techmate.application.usecase.material;

import com.techmate.techmate.domain.model.material.Material;
import com.techmate.techmate.domain.port.in.UpdateMaterialUseCase;
import com.techmate.techmate.domain.port.out.MaterialRepositoryPort;
import com.techmate.techmate.domain.port.out.SubCategoryRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 🎯 USE CASE - UpdateMaterialUseCaseImpl
 * 
 * Implementa la actualización de materiales.
 * 
 * @author TechShare Team - Hexagonal Architecture
 * @version 2.0.0
 */
@Service
@Transactional
public class UpdateMaterialUseCaseImpl implements UpdateMaterialUseCase {

    private static final Logger log = LoggerFactory.getLogger(UpdateMaterialUseCaseImpl.class);

    private final MaterialRepositoryPort materialRepository;
    private final SubCategoryRepositoryPort subCategoryRepository;

    public UpdateMaterialUseCaseImpl(
            MaterialRepositoryPort materialRepository,
            SubCategoryRepositoryPort subCategoryRepository) {
        this.materialRepository = materialRepository;
        this.subCategoryRepository = subCategoryRepository;
    }

    @Override
    public Material updateMaterial(Integer id, UpdateMaterialRequest request) {
        log.info("🔄 Actualizando material ID: {}", id);

        // Verificar que el material existe
        Material existingMaterial = materialRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                String.format("Material con ID %d no existe", id)));

        // Validar que la subcategoría existe
        if (!subCategoryRepository.existsById(request.subCategoryId())) {
            throw new IllegalArgumentException(
                String.format("La subcategoría con ID %d no existe", request.subCategoryId()));
        }

        // Crear material actualizado
        Material updatedMaterial = Material.builder()
            .id(id)
            .name(request.name())
            .description(request.description())
            .price(request.price())
            .stock(request.stock())
            .borrowableStock(request.borrowableStock())
            .imagePath(request.imagePath())
            .subCategoryId(request.subCategoryId())
            .build();

        Material saved = materialRepository.save(updatedMaterial);
        log.info("✅ Material actualizado exitosamente: {}", id);
        return saved;
    }

    @Override
    public Material increaseStock(Integer id, int quantity) {
        log.info("📈 Aumentando stock del material ID: {} en {} unidades", id, quantity);

        Material material = materialRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                String.format("Material con ID %d no existe", id)));

        Material updatedMaterial = material.increaseStock(quantity);
        Material saved = materialRepository.save(updatedMaterial);
        
        log.info("✅ Stock aumentado. Nuevo stock: {}, Nuevo borrowable: {}", 
            saved.getStock(), saved.getBorrowableStock());
        return saved;
    }

    @Override
    public Material reduceBorrowableStock(Integer id, int quantity) {
        log.info("📉 Reduciendo stock prestable del material ID: {} en {} unidades", id, quantity);

        Material material = materialRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                String.format("Material con ID %d no existe", id)));

        Material updatedMaterial = material.reduceBorrowableStock(quantity);
        Material saved = materialRepository.save(updatedMaterial);
        
        log.info("✅ Stock prestable reducido. Nuevo borrowable: {}", saved.getBorrowableStock());
        return saved;
    }

    @Override
    public Material restoreBorrowableStock(Integer id, int quantity) {
        log.info("📈 Restaurando stock prestable del material ID: {} en {} unidades", id, quantity);

        Material material = materialRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                String.format("Material con ID %d no existe", id)));

        Material updatedMaterial = material.restoreBorrowableStock(quantity);
        Material saved = materialRepository.save(updatedMaterial);
        
        log.info("✅ Stock prestable restaurado. Nuevo borrowable: {}", saved.getBorrowableStock());
        return saved;
    }

    @Override
    public void deleteMaterial(Integer id) {
        log.info("🗑️ Eliminando material ID: {}", id);

        if (!materialRepository.existsById(id)) {
            throw new IllegalArgumentException(
                String.format("Material con ID %d no existe", id));
        }

        materialRepository.deleteById(id);
        log.info("✅ Material eliminado exitosamente: {}", id);
    }
}
