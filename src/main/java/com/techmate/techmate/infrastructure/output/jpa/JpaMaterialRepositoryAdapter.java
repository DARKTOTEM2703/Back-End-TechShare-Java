package com.techmate.techmate.infrastructure.output.jpa;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.techmate.techmate.core.application.port.output.MaterialRepositoryPort;
import com.techmate.techmate.infrastructure.persistence.repository.MaterialsRepository;
import com.techmate.techmate.infrastructure.persistence.entity.Materials;

import java.util.List;
import java.util.Optional;

@Component
@Transactional
public class JpaMaterialRepositoryAdapter implements MaterialRepositoryPort {

    private final MaterialsRepository materialsRepository;

    public JpaMaterialRepositoryAdapter(MaterialsRepository materialsRepository) {
        this.materialsRepository = materialsRepository;
    }

    @Override
    public Materials save(Materials material) {
        return materialsRepository.save(material);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Materials> findById(Long id) {
        if (id == null)
            return Optional.empty();
        return materialsRepository.findById(id.intValue());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Materials> findAll() {
        return materialsRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        if (id == null)
            return;
        materialsRepository.deleteById(id.intValue());
    }
}
