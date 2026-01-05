package com.techmate.techmate.core.application.port.input;

import com.techmate.techmate.infrastructure.dto.RegisterRequest;
import com.techmate.techmate.infrastructure.dto.AuthUserDTO;
import com.techmate.techmate.infrastructure.dto.UsuarioDTO;

public interface UserManagementUseCase {
    UsuarioDTO register(RegisterRequest request);

    UsuarioDTO login(AuthUserDTO request);

    UsuarioDTO getUserById(Long id);

    UsuarioDTO updateUser(Long id, UsuarioDTO userDto);
}






