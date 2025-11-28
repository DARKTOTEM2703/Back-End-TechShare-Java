package com.techmate.techmate.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.techmate.techmate.dto.UsuarioDTO;
import com.techmate.techmate.entity.Role;
import com.techmate.techmate.entity.Usuario;
import com.techmate.techmate.repository.RoleRepository;
import com.techmate.techmate.repository.UsuarioRepository;
import com.techmate.techmate.repository.UsuarioRoleRepository;
import com.techmate.techmate.service.UserService;
import com.techmate.techmate.service.User.mapper.UserMapper;
import com.techmate.techmate.service.User.query.UserQueryService;
import com.techmate.techmate.service.User.validator.UserValidator;

@Service
public class UserServiceImpl implements UserService {

    private final UsuarioRepository userRepository;
    private final RoleRepository roleRepository; // utilizado para resolución de roles en update
    private final UsuarioRoleRepository usuarioRoleRepository;
    private final UserMapper userMapper;
    private final UserValidator userValidator;
    private final UserQueryService userQueryService;

    public UserServiceImpl(UsuarioRepository userRepository,
            RoleRepository roleRepository,
            UsuarioRoleRepository usuarioRoleRepository,
            UserMapper userMapper,
            UserValidator userValidator,
            UserQueryService userQueryService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.usuarioRoleRepository = usuarioRoleRepository;
        this.userMapper = userMapper;
        this.userValidator = userValidator;
        this.userQueryService = userQueryService;
    }

    @Override
    public List<UsuarioDTO> getAllUser() {
        return userQueryService.getAllUsers();
    }

    @Override
    public Optional<UsuarioDTO> findUserById(Integer id) {
        return userRepository.findById(id)
                .map(u -> Optional.of(userMapper.toDTO(u, usuarioRoleRepository.findByUsuarioIds(List.of(id)).stream()
                        .map(usuarioRole -> usuarioRole.getRole().getNombre()).collect(Collectors.toSet()))))
                .orElse(Optional.empty());
    }

    @Override
    public void deleteUsuser(Integer id) {
        Optional<Usuario> usuarioOpt = userRepository.findById(id);

        if (usuarioOpt.isEmpty()) {
            throw new com.techmate.techmate.exception.NotFoundException(
                    "El usuario con ID " + id + " no fue encontrado.");
        }

        userRepository.deleteById(id);
    }

    @Override
    public Optional<UsuarioDTO> updateUser(Integer id, UsuarioDTO usuarioDTO) {
        // Buscar al usuario por ID
        Usuario usuario = userRepository.findById(id)
                .orElseThrow(() -> new com.techmate.techmate.exception.NotFoundException(
                        "Usuario no encontrado con ID: " + id));

        // Actualizar los campos básicos del usuario
        usuario.setUser_name(usuarioDTO.getUserName());
        usuario.setFirst_name(usuarioDTO.getFirstName());
        usuario.setLast_name(usuarioDTO.getLastName());
        usuario.setEmail(usuarioDTO.getEmail());

        // Validar roles
        userValidator.validateRolesExist(usuarioDTO.getRoles());

        // Manejar la actualización de roles
        Set<Role> updatedRoles = usuarioDTO.getRoles().stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + roleName)))
                .collect(Collectors.toSet());

        // Limpiar los roles antiguos y asignar los nuevos
        usuario.getRoles().clear();
        usuario.getRoles().addAll(updatedRoles);

        Usuario usuarioActualizado = userRepository.save(usuario);

        Set<String> rolesActualizados = usuarioActualizado.getRoles().stream()
                .map(Role::getNombre)
                .collect(Collectors.toSet());

        return Optional.of(userMapper.toDTO(usuarioActualizado, rolesActualizados));
    }

}
