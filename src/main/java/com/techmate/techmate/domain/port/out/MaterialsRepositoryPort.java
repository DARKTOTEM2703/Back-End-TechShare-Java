package com.techmate.techmate.domain.port.out;

import com.techmate.techmate.domain.model.Material;

import java.util.Optional;
import java.util.List;

public interface MaterialsRepositoryPort {

    Optional<Material> findById(Integer id);

    Material save(Material material);

    List<Material> findAllByIds(List<Integer> ids);
}
