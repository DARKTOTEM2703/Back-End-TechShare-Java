package com.techmate.techmate.application.usecase.material;

import com.techmate.techmate.domain.model.material.Material;
import com.techmate.techmate.domain.port.in.CreateMaterialUseCase;
import com.techmate.techmate.domain.port.out.MaterialRepositoryPort;
import com.techmate.techmate.domain.port.out.SubCategoryRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 🎯 USE CASE - CreateMaterialUseCaseImpl
 * 
 * Implementa la creación de materiales.
 * Valida reglas de negocio y persiste el material.
 * 
 * @author TechShare Team - Hexagonal Architecture
 * @version 2.0.0
 */
@Service
@Transactional
public class CreateMaterialUseCaseImpl implements CreateMaterialUseCase {

    private static final Logger log = LoggerFactory.getLogger(CreateMaterialUseCaseImpl.class);

    private final MaterialRepositoryPort materialRepository;
    private final SubCategoryRepositoryPort subCategoryRepository;

    public CreateMaterialUseCaseImpl(
            MaterialRepositoryPort materialRepository,
            SubCategoryRepositoryPort subCategoryRepository) {
        this.materialRepository = materialRepository;
        this.subCategoryRepository = subCategoryRepository;
    }

    @Override
    public Material createMaterial(CreateMaterialRequest request) {
        log.info("📦 Creando nuevo material: {}", request.name());

        // Validar que la subcategoría existe
        if (!subCategoryRepository.existsById(request.subCategoryId())) {
            throw new IllegalArgumentException(
                String.format("La subcategoría con ID %d no existe", request.subCategoryId()));
        }

        // Crear el material usando el builder del dominio
        Material material = Material.builder()
            .name(request.name())
            .description(request.description())
            .price(request.price())
            .stock(request.stock())
            .borrowableStock(request.borrowableStock())
            .imagePath(request.imagePath())
            .subCategoryId(request.subCategoryId())
            .build();

        // Persistir
        Material savedMaterial = materialRepository.save(material);
        
        log.info("✅ Material creado exitosamente con ID: {}", savedMaterial.getId());
        return savedMaterial;
    }
}
