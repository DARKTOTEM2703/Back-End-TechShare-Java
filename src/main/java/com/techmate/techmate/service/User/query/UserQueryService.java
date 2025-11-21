package com.techmate.techmate.service.User.query;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.techmate.techmate.dto.UsuarioDTO;
import com.techmate.techmate.entity.Usuario;
import com.techmate.techmate.entity.UsuarioRole;
import com.techmate.techmate.repository.UsuarioRepository;
import com.techmate.techmate.repository.UsuarioRoleRepository;
import com.techmate.techmate.service.User.mapper.UserMapper;

@Component
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
    if (usuarios.isEmpty()) throw new com.techmate.techmate.exception.NotFoundException("No está disponible ningún usuario");

        List<Integer> usuarioIds = usuarios.stream().map(Usuario::getId).collect(Collectors.toList());
        List<UsuarioRole> usuarioRoles = usuarioRoleRepository.findByUsuarioIds(usuarioIds);

        return usuarios.stream().map(usuario -> {
            Set<String> roles = usuarioRoles.stream()
                    .filter(ur -> ur.getUsuario().getId().equals(usuario.getId()))
                    .map(ur -> ur.getRole().getNombre())
                    .collect(Collectors.toSet());
            if (roles.stream().anyMatch(role -> role.equalsIgnoreCase("root"))) return null;
            return userMapper.toDTO(usuario, roles);
        }).filter(r -> r != null).collect(Collectors.toList());
    }

    public UsuarioDTO getById(Integer id) {
        return usuarioRepository.findById(id).map(u -> {
            Set<String> roles = usuarioRoleRepository.findByUsuarioIds(List.of(id)).stream()
                    .map(ur -> ur.getRole().getNombre())
                    .collect(Collectors.toSet());
            return userMapper.toDTO(u, roles);
    }).orElseThrow(() -> new com.techmate.techmate.exception.NotFoundException("Usuario no encontrado con ID: " + id));
    }
}

