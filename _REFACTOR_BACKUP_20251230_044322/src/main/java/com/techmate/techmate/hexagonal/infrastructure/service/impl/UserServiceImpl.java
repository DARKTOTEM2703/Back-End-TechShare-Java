package com.techmate.techmate.hexagonal.infrastructure.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.techmate.techmate.hexagonal.infrastructure.dto.UsuarioDTO;
import com.techmate.techmate.hexagonal.domain.entity.Role;
import com.techmate.techmate.hexagonal.domain.entity.Usuario;
import com.techmate.techmate.hexagonal.infrastructure.service.UserService;
import com.techmate.techmate.hexagonal.infrastructure.service.User.manager.UserManager;
import com.techmate.techmate.hexagonal.infrastructure.service.User.mapper.UserMapper;
import com.techmate.techmate.hexagonal.infrastructure.service.User.query.UserQueryService;

@Service
public class UserServiceImpl implements UserService {

    private final UserQueryService userQueryService;
    private final UserManager userManager;
    private final UserMapper userMapper;

    public UserServiceImpl(UserQueryService userQueryService,
            UserManager userManager,
            UserMapper userMapper) {
        this.userQueryService = userQueryService;
        this.userManager = userManager;
        this.userMapper = userMapper;
    }

    @Override
    public List<UsuarioDTO> getAllUser() {
        return userQueryService.getAllUsers();
    }

    @Override
    public Optional<UsuarioDTO> findUserById(Integer id) {
        return userQueryService.findDTOById(id);
    }

    @Override
    public void deleteUsuser(Integer id) {
        userManager.deleteUser(id);
    }

    @Override
    public Optional<UsuarioDTO> updateUser(Integer id, UsuarioDTO usuarioDTO) {
        // Delegar la lógica de actualización al manager
        Usuario usuarioActualizado = userManager.updateUser(id, usuarioDTO);

        // Mapear la entidad actualizada de vuelta a DTO
        // Extraemos los nombres de los roles directamente de la entidad actualizada
        Set<String> rolesActualizados = usuarioActualizado.getRoles().stream()
                .map(Role::getNombre)
                .collect(Collectors.toSet());

        return Optional.of(userMapper.toDTO(usuarioActualizado, rolesActualizados));
    }

}







