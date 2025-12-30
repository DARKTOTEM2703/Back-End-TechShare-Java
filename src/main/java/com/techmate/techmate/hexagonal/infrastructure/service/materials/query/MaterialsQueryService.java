package com.techmate.techmate.hexagonal.infrastructure.service.materials.query;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import com.techmate.techmate.hexagonal.infrastructure.dto.MaterialsDTO;
import com.techmate.techmate.hexagonal.domain.entity.Materials;
import com.techmate.techmate.hexagonal.domain.repository.MaterialsRepository;
import com.techmate.techmate.hexagonal.infrastructure.service.materials.mapper.MaterialsMapper;

/**
 * Servicio de consultas para Materials (SRP).
 */
@Component
public class MaterialsQueryService {

    private final MaterialsRepository materialsRepository;
    private final MaterialsMapper materialsMapper;

    public MaterialsQueryService(MaterialsRepository materialsRepository, MaterialsMapper materialsMapper) {
        this.materialsRepository = materialsRepository;
        this.materialsMapper = materialsMapper;
    }

    @Transactional(readOnly = true)
    public List<MaterialsDTO> getAllMaterials() {
        return materialsRepository.findAll().stream()
                .map(materialsMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MaterialsDTO> getAllMaterialsSortedByPrice(boolean ascending) {
        List<Materials> materials = ascending ? materialsRepository.findAllByOrderByPriceAsc()
                : materialsRepository.findAllByOrderByPriceDesc();
        return materials.stream().map(materialsMapper::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MaterialsDTO getById(int id) {
        Materials m = materialsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material no encontrado con ID: " + id));
        return materialsMapper.toDTO(m);
    }
}








