package com.techmate.techmate.hexagonal.application.port.input;

import com.techmate.techmate.dto.MaterialRequest;
import com.techmate.techmate.dto.MaterialResponse;
import java.util.List;

public interface MaterialManagementUseCase {
    MaterialResponse createMaterial(MaterialRequest request);

    MaterialResponse getMaterialById(Long id);

    List<MaterialResponse> getAllMaterials();

    MaterialResponse updateMaterial(Long id, MaterialRequest request);

    void deleteMaterial(Long id);
}
