package com.techmate.techmate.core.application.usecase;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.techmate.techmate.core.application.port.input.MaterialManagementUseCase;
import com.techmate.techmate.infrastructure.dto.MaterialRequest;
import com.techmate.techmate.infrastructure.dto.MaterialResponse;
import com.techmate.techmate.infrastructure.dto.MaterialsDTO;
import com.techmate.techmate.infrastructure.service.MaterialsService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MaterialManagementUseCaseImpl implements MaterialManagementUseCase {

    private final MaterialsService materialsService;

    private MaterialsDTO toDto(MaterialRequest req) {
        MaterialsDTO dto = new MaterialsDTO();
        dto.setName(req.getName());
        dto.setDescription(req.getDescription() != null ? req.getDescription() : "");
        dto.setPrice(req.getPrice() != null ? BigDecimal.valueOf(req.getPrice()) : BigDecimal.ZERO);
        dto.setStock(req.getStock() != null ? req.getStock() : 0);
        dto.setBorrowableStock(0);
        dto.setSubCategoryId(req.getSubCategoryId() != null ? req.getSubCategoryId() : 1);
        if (req.getRoleIds() != null)
            dto.setRoleIds(req.getRoleIds());
        return dto;
    }

    private MaterialResponse toResponse(MaterialsDTO dto) {
        if (dto == null)
            return null;
        MaterialResponse r = new MaterialResponse();
        r.setMaterialsId(dto.getId());
        r.setImagePath(dto.getImagePath());
        r.setName(dto.getName());
        r.setDescription(dto.getDescription());
        r.setPrice(dto.getPrice());
        r.setStock(dto.getStock());
        r.setBorrowable_stock(dto.getBorrowableStock());
        r.setSubCategoryId(dto.getSubCategoryId());
        r.setSubCategoryName(dto.getSubCategoryName());
        r.setRoleNames(dto.getRoleNames());
        return r;
    }

    @Override
    public MaterialResponse createMaterial(MaterialRequest request, MultipartFile image) {
        MaterialsDTO dto = toDto(request);
        MaterialsDTO created = materialsService.createMaterials(dto, image);
        return toResponse(created);
    }

    @Override
    @Transactional(readOnly = true)
    public MaterialResponse getMaterialById(Long id) {
        MaterialsDTO dto = materialsService.getMaterialsById(id.intValue());
        return toResponse(dto);
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<MaterialResponse> getAllMaterials() {
        List<MaterialsDTO> list = materialsService.getAllMaterials();
        return list.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public MaterialResponse updateMaterial(Long id, MaterialRequest request) {
        MaterialsDTO dto = toDto(request);
        MaterialsDTO updated = materialsService.updateMaterials(id.intValue(), dto, null);
        return toResponse(updated);
    }

    @Override
    public void deleteMaterial(Long id) {
        materialsService.deleteMaterials(id.intValue());
    }
}
