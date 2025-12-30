package com.techmate.techmate.hexagonal.application.port.output;

import com.techmate.techmate.hexagonal.domain.entity.Usuario;
import java.util.Optional;

public interface UserRepositoryPort {
    Usuario save(Usuario user);

    Optional<Usuario> findById(Long id);

    Optional<Usuario> findByUsername(String username);

    Optional<Usuario> findByEmail(String email);
}





