package com.techmate.techmate.service.User.manager;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.techmate.techmate.dto.UsuarioDTO;
import com.techmate.techmate.entity.Role;
import com.techmate.techmate.entity.Usuario;
import com.techmate.techmate.repository.RoleRepository;
import com.techmate.techmate.repository.UsuarioRepository;
import com.techmate.techmate.service.User.validator.UserValidator;

@Component
public class UserManager {

    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final UserValidator userValidator;

    public UserManager(UsuarioRepository usuarioRepository, RoleRepository roleRepository,
            UserValidator userValidator) {
        this.usuarioRepository = usuarioRepository;
        this.roleRepository = roleRepository;
        this.userValidator = userValidator;
    }

    @Transactional
    public void deleteUser(Integer id) {
        if (!usuarioRepository.existsById(id)) {
            throw new com.techmate.techmate.exception.NotFoundException(
                    "El usuario con ID " + id + " no fue encontrado.");
        }
        usuarioRepository.deleteById(id);
    }

    @Transactional
    public Usuario updateUser(Integer id, UsuarioDTO usuarioDTO) {
        // Buscar al usuario por ID
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new com.techmate.techmate.exception.NotFoundException(
                        "Usuario no encontrado con ID: " + id));

        // Actualizar los campos básicos del usuario
        usuario.setUser_name(usuarioDTO.getUserName());
        usuario.setFirst_name(usuarioDTO.getFirstName());
        usuario.setLast_name(usuarioDTO.getLastName());
        usuario.setEmail(usuarioDTO.getEmail());

        // Validar roles usando el validador existente
        userValidator.validateRolesExist(usuarioDTO.getRoles());

        // Manejar la actualización de roles
        Set<Role> updatedRoles = usuarioDTO.getRoles().stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + roleName)))
                .collect(Collectors.toSet());

        // Limpiar los roles antiguos y asignar los nuevos
        usuario.getRoles().clear();
        usuario.getRoles().addAll(updatedRoles);

        return usuarioRepository.save(usuario);
    }
}
