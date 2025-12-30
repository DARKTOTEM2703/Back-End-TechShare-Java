package com.techmate.techmate.infrastructure.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.techmate.techmate.infrastructure.dto.MaterialsDTO;


public interface MaterialsService {

    MaterialsDTO createMaterials(MaterialsDTO materialsDTO, MultipartFile iMultipartFile);
    MaterialsDTO getMaterialsById(int materialsId);
    MaterialsDTO updateMaterials(int materialsId, MaterialsDTO materialsDTO, MultipartFile iMultipartFile);
    void deleteMaterials(int materialsId);
    List<MaterialsDTO> getAllMaterials();
    Page<MaterialsDTO> getAllMaterialsPaginated(Pageable pageable);
    List<MaterialsDTO> getAllMaterialsSortedByPrice(boolean ascending);

    
   String getMaterialsNameById(int materialId);


   
}









