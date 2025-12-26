package com.techmate.techmate.infra.adapter.jpa;

import com.techmate.techmate.domain.port.out.UsuarioRepositoryPort;
import com.techmate.techmate.entity.Usuario;
import com.techmate.techmate.repository.UsuarioRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class JpaUsuarioRepositoryAdapter implements UsuarioRepositoryPort {

    private final UsuarioRepository usuarioRepository;

    public JpaUsuarioRepositoryAdapter(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Optional<Usuario> findById(Integer id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public List<Usuario> findAllByIds(List<Integer> ids) {
        List<Usuario> list = usuarioRepository.findAllById(ids);
        return list.stream().collect(Collectors.toList());
    }
}
