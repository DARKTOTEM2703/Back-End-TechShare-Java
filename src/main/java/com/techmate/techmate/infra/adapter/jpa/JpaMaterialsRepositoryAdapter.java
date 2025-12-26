package com.techmate.techmate.infra.adapter.jpa;

import com.techmate.techmate.domain.model.Material;
import com.techmate.techmate.domain.port.out.MaterialsRepositoryPort;
import com.techmate.techmate.repository.MaterialsRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JpaMaterialsRepositoryAdapter implements MaterialsRepositoryPort {

    private final MaterialsRepository materialsRepository;

    public JpaMaterialsRepositoryAdapter(MaterialsRepository materialsRepository) {
        this.materialsRepository = materialsRepository;
    }

    @Override
    public Optional<Material> findById(Integer id) {
        return materialsRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Material save(Material material) {
        com.techmate.techmate.entity.Materials jpa = new com.techmate.techmate.entity.Materials();
        jpa.setId(material.getId());
        jpa.setName(material.getName());
        jpa.setPrice(material.getPrice());
        jpa.setBorrowableStock(material.getBorrowableStock());
        com.techmate.techmate.entity.Materials saved = materialsRepository.save(jpa);
        return toDomain(saved);
    }

    @Override
    public List<Material> findAllByIds(List<Integer> ids) {
        List<com.techmate.techmate.entity.Materials> found = materialsRepository.findAllById(ids);
        return found.stream().map(this::toDomain).collect(Collectors.toList());
    }

    private Material toDomain(com.techmate.techmate.entity.Materials j) {
        if (j == null)
            return null;
        Material m = new Material();
        m.setId(j.getId());
        m.setName(j.getName());
        m.setPrice(j.getPrice());
        m.setBorrowableStock(j.getBorrowableStock());
        return m;
    }
}
