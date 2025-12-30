package com.techmate.techmate.hexagonal.application.port.output;

import com.techmate.techmate.hexagonal.domain.entity.Materials;
import java.util.Optional;
import java.util.List;

public interface MaterialRepositoryPort {
    Materials save(Materials material);

    Optional<Materials> findById(Long id);

    List<Materials> findAll();

    void delete(Long id);
}





