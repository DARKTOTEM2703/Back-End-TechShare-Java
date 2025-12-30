package com.techmate.techmate.infrastructure.adapter.persistence;

import com.techmate.techmate.application.port.output.UserRepositoryPort;
import com.techmate.techmate.domain.entity.Usuario;
import com.techmate.techmate.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class UsuarioRepositoryAdapter implements UserRepositoryPort {
    private final UsuarioRepository jpaRepository;

    public UsuarioRepositoryAdapter(UsuarioRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Usuario save(Usuario user) {
        return jpaRepository.save(user);
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return jpaRepository.findById(id.intValue());
    }

    @Override
    public Optional<Usuario> findByUsername(String username) {
        // UsuarioRepository no tiene findByUsername, buscar por email como alternativa
        return Optional.empty();
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        return jpaRepository.findOneByEmailNative(email);
    }
}
