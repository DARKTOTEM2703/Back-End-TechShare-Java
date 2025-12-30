package com.techmate.techmate.application.port.output;

import com.techmate.techmate.domain.entity.Usuario;
import java.util.Optional;

public interface UserRepositoryPort {
    Usuario save(Usuario user);

    Optional<Usuario> findById(Long id);

    Optional<Usuario> findByUsername(String username);

    Optional<Usuario> findByEmail(String email);
}






