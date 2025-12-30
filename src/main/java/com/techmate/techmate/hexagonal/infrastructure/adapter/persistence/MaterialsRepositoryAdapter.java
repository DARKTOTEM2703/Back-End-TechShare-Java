package com.techmate.techmate.hexagonal.infrastructure.adapter.persistence;

import com.techmate.techmate.hexagonal.application.port.output.MaterialRepositoryPort;
import com.techmate.techmate.hexagonal.domain.entity.Materials;
import com.techmate.techmate.hexagonal.domain.repository.MaterialsRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.List;

@Component
public class MaterialsRepositoryAdapter implements MaterialRepositoryPort {
    private final MaterialsRepository jpaRepository;

    public MaterialsRepositoryAdapter(MaterialsRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Materials save(Materials material) {
        return jpaRepository.save(material);
    }

    @Override
    public Optional<Materials> findById(Long id) {
        return jpaRepository.findById(id.intValue());
    }

    @Override
    public List<Materials> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        jpaRepository.deleteById(id.intValue());
    }
}
