package com.techmate.techmate.infra.adapter.output.jpa;

import com.techmate.techmate.domain.model.material.Material;
import com.techmate.techmate.domain.port.out.MaterialRepositoryPort;
import com.techmate.techmate.hexagonal.domain.entity.Materials;
import com.techmate.techmate.infra.mapper.DomainMaterialMapper;
import com.techmate.techmate.hexagonal.domain.repository.MaterialsRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 🔌 JPA ADAPTER - JpaMaterialRepositoryAdapter
 * 
 * Implementa MaterialRepositoryPort usando JPA.
 * Convierte entre domain models (Material) y entities (Materials).
 * 
 * PATRÓN: Adapter (Ports & Adapters)
 * Esta clase vive en la infraestructura y conecta el dominio con JPA.
 * 
 * @author TechShare Team - Hexagonal Architecture
 * @version 2.0.0
 */
@Component
public class JpaMaterialRepositoryAdapter implements MaterialRepositoryPort {

    private final MaterialsRepository jpaRepository;
    private final DomainMaterialMapper mapper;

    public JpaMaterialRepositoryAdapter(
            MaterialsRepository jpaRepository,
            DomainMaterialMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Material save(Material material) {
        Materials entity = mapper.toEntity(material);
        Materials saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Material> findById(Integer id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<Material> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Material> findBySubCategoryId(Integer subCategoryId) {
        return jpaRepository.findBySubCategoryId(subCategoryId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Material> findByNameContaining(String name) {
        return jpaRepository.findByNameContainingIgnoreCase(name).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Material> findAvailableForBorrow() {
        return jpaRepository.findByBorrowableStockGreaterThan(0).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Integer id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Integer id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public List<Material> findBySubCategoryIdPaginated(Integer subCategoryId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return jpaRepository.findBySubCategoryIdWithPagination(subCategoryId, pageRequest)
                .map(mapper::toDomain)
                .getContent();
    }
}
