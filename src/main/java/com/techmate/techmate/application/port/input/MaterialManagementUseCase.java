package com.techmate.techmate.application.port.input;

import com.techmate.techmate.infrastructure.dto.MaterialRequest;
import com.techmate.techmate.infrastructure.dto.MaterialResponse;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/**
 * Puerto de entrada para gestión de materials (UseCases).
 * El método createMaterial ahora acepta el archivo multipart para que el caso
 * de uso
 * sea responsable del flujo de almacenamiento de imágenes (hexagonal boundary).
 */
public interface MaterialManagementUseCase {
    MaterialResponse createMaterial(MaterialRequest request, MultipartFile image);

    MaterialResponse getMaterialById(Long id);

    List<MaterialResponse> getAllMaterials();

    MaterialResponse updateMaterial(Long id, MaterialRequest request);

    void deleteMaterial(Long id);
}
