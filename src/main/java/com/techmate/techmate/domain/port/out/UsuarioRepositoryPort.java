package com.techmate.techmate.domain.port.out;

import com.techmate.techmate.entity.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepositoryPort {
    Optional<Usuario> findById(Integer id);

    List<Usuario> findAllByIds(List<Integer> ids);
}
