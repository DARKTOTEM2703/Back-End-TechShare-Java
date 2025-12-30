package com.techmate.techmate.application.usecase.material;

import com.techmate.techmate.domain.model.material.Material;
import com.techmate.techmate.domain.port.in.GetMaterialsUseCase;
import com.techmate.techmate.domain.port.out.MaterialRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 🎯 USE CASE - GetMaterialsUseCaseImpl
 * 
 * Implementa las consultas de materiales.
 * Orquesta la lógica de negocio usando los ports.
 * 
 * @author TechShare Team - Hexagonal Architecture
 * @version 2.0.0
 */
@Service
@Transactional(readOnly = true)
public class GetMaterialsUseCaseImpl implements GetMaterialsUseCase {

    private final MaterialRepositoryPort materialRepository;

    public GetMaterialsUseCaseImpl(MaterialRepositoryPort materialRepository) {
        this.materialRepository = materialRepository;
    }

    @Override
    public List<Material> getAllMaterials() {
        return materialRepository.findAll();
    }

    @Override
    public Optional<Material> getMaterialById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID del material debe ser válido");
        }
        return materialRepository.findById(id);
    }

    @Override
    public List<Material> getMaterialsBySubCategory(Integer subCategoryId) {
        if (subCategoryId == null || subCategoryId <= 0) {
            throw new IllegalArgumentException("El ID de la subcategoría debe ser válido");
        }
        return materialRepository.findBySubCategoryId(subCategoryId);
    }

    @Override
    public List<Material> searchMaterialsByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de búsqueda no puede estar vacío");
        }
        return materialRepository.findByNameContaining(name.trim());
    }

    @Override
    public List<Material> getAvailableMaterialsForBorrow() {
        return materialRepository.findAvailableForBorrow();
    }

    @Override
    public List<Material> getMaterialsBySubCategoryPaginated(Integer subCategoryId, int page, int size) {
        if (subCategoryId == null || subCategoryId <= 0) {
            throw new IllegalArgumentException("El ID de la subcategoría debe ser válido");
        }
        if (page < 0) {
            throw new IllegalArgumentException("El número de página no puede ser negativo");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("El tamaño de página debe ser positivo");
        }
        
        return materialRepository.findBySubCategoryIdPaginated(subCategoryId, page, size);
    }
}
