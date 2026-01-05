package com.techmate.techmate.infrastructure.service.User.query;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.techmate.techmate.infrastructure.dto.UsuarioDTO;
import com.techmate.techmate.infrastructure.persistence.entity.Usuario;
import com.techmate.techmate.infrastructure.persistence.entity.UsuarioRole;
import com.techmate.techmate.infrastructure.persistence.repository.UsuarioRepository;
import com.techmate.techmate.infrastructure.persistence.repository.UsuarioRoleRepository;
import com.techmate.techmate.infrastructure.service.User.mapper.UserMapper;

@Component
@Transactional(readOnly = true)
public class UserQueryService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioRoleRepository usuarioRoleRepository;
    private final UserMapper userMapper;

    public UserQueryService(UsuarioRepository usuarioRepository,
            UsuarioRoleRepository usuarioRoleRepository,
            UserMapper userMapper) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioRoleRepository = usuarioRoleRepository;
        this.userMapper = userMapper;
    }

    public List<UsuarioDTO> getAllUsers() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        if (usuarios.isEmpty()) {
            throw new com.techmate.techmate.infrastructure.exception.NotFoundException("No está disponible ningún usuario");
        }

        List<Integer> usuarioIds = usuarios.stream().map(Usuario::getId).collect(Collectors.toList());
        List<UsuarioRole> usuarioRoles = usuarioRoleRepository.findByUsuarioIds(usuarioIds);

        return usuarios.stream().map(usuario -> {
            Set<String> roles = usuarioRoles.stream()
                    .filter(ur -> ur.getUsuario().getId().equals(usuario.getId()))
                    .map(ur -> ur.getRole().getNombre())
                    .collect(Collectors.toSet());
            // Filtrar usuarios root
            if (roles.stream().anyMatch(role -> role.equalsIgnoreCase("root")))
                return null;
            return userMapper.toDTO(usuario, roles);
        }).filter(r -> r != null).collect(Collectors.toList());
    }

    public UsuarioDTO getById(Integer id) {
        return findDTOById(id)
                .orElseThrow(() -> new com.techmate.techmate.infrastructure.exception.NotFoundException(
                        "Usuario no encontrado con ID: " + id));
    }

    // Nuevo método helper para devolver Optional y ser usado por el Service
    public Optional<UsuarioDTO> findDTOById(Integer id) {
        return usuarioRepository.findById(id).map(u -> {
            Set<String> roles = usuarioRoleRepository.findByUsuarioIds(List.of(id)).stream()
                    .map(ur -> ur.getRole().getNombre())
                    .collect(Collectors.toSet());
            return userMapper.toDTO(u, roles);
        });
    }
}







