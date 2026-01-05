package com.techmate.techmate.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.io.IOException;

import com.techmate.techmate.core.application.port.input.MaterialManagementUseCase;
import com.techmate.techmate.core.application.port.output.ImageStoragePort;
import com.techmate.techmate.core.application.port.output.MaterialRepositoryPort;
import com.techmate.techmate.infrastructure.dto.MaterialRequest;
import com.techmate.techmate.infrastructure.dto.MaterialResponse;
import com.techmate.techmate.infrastructure.service.materials.mapper.MaterialsMapper;
import com.techmate.techmate.infrastructure.service.materials.validator.MaterialsValidator;
import com.techmate.techmate.infrastructure.config.AppProperties;
import com.techmate.techmate.infrastructure.persistence.entity.Materials;
import com.techmate.techmate.infrastructure.dto.MaterialsDTO;
import com.techmate.techmate.core.application.exception.ImageStorageException;

@Service
@Transactional
public class MaterialManagementService implements MaterialManagementUseCase {

    private final MaterialsValidator materialsValidator;
    private final MaterialsMapper materialsMapper;
    private final ImageStoragePort imageStoragePort;
    private final MaterialRepositoryPort materialRepositoryPort;
    private final AppProperties appProperties;

    public MaterialManagementService(MaterialsValidator materialsValidator,
            MaterialsMapper materialsMapper,
            ImageStoragePort imageStoragePort,
            MaterialRepositoryPort materialRepositoryPort,
            AppProperties appProperties) {
        this.materialsValidator = materialsValidator;
        this.materialsMapper = materialsMapper;
        this.imageStoragePort = imageStoragePort;
        this.materialRepositoryPort = materialRepositoryPort;
        this.appProperties = appProperties;
    }

    @Override
    public MaterialResponse createMaterial(MaterialRequest request, MultipartFile image) {
        if (request == null)
            throw new IllegalArgumentException("MaterialRequest must not be null");

        // Validaciones de negocio
        materialsValidator.validateUniqueName(request.getName());
        materialsValidator.validateSubCategoryExists(request.getSubCategoryId());
        materialsValidator.validateRolesExist(request.getRoleIds());

        // Mapear request a DTO interno
        MaterialsDTO dto = materialsMapper.fromRequest(request);

        // Si viene imagen, subirla y asignar path/url
        if (image != null && !image.isEmpty()) {
            try {
                String uploaded = imageStoragePort.saveImage(image, "materials");
                dto.setImagePath(uploaded);
            } catch (ImageStorageException e) {
                throw e;
            }
        }

        // Convertir DTO a entidad de dominio
        Materials entity = materialsMapper.toEntity(dto);

        // Persistir por el puerto de salida
        Materials saved = materialRepositoryPort.save(entity);

        // Convertir a DTO de respuesta
        MaterialsDTO savedDto = materialsMapper.toDTO(saved);
        return materialsMapper.toResponse(savedDto, appProperties.getServerUrl());
    }

    @Override
    public MaterialResponse getMaterialById(Long id) {
        throw new UnsupportedOperationException("Not implemented in this patch");
    }

    @Override
    public java.util.List<MaterialResponse> getAllMaterials() {
        throw new UnsupportedOperationException("Not implemented in this patch");
    }

    @Override
    public MaterialResponse updateMaterial(Long id, MaterialRequest request) {
        throw new UnsupportedOperationException("Not implemented in this patch");
    }

    @Override
    public void deleteMaterial(Long id) {
        throw new UnsupportedOperationException("Not implemented in this patch");
    }
}
