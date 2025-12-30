package com.techmate.techmate.hexagonal.application.port.input;

import com.techmate.techmate.hexagonal.infrastructure.dto.RegisterRequest;
import com.techmate.techmate.hexagonal.infrastructure.dto.AuthUserDTO;
import com.techmate.techmate.hexagonal.infrastructure.dto.UsuarioDTO;

public interface UserManagementUseCase {
    UsuarioDTO register(RegisterRequest request);

    UsuarioDTO login(AuthUserDTO request);

    UsuarioDTO getUserById(Long id);

    UsuarioDTO updateUser(Long id, UsuarioDTO userDto);
}






