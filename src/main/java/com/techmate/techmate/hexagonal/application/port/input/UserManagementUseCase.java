package com.techmate.techmate.hexagonal.application.port.input;

import com.techmate.techmate.dto.RegisterRequest;
import com.techmate.techmate.dto.AuthUserDTO;
import com.techmate.techmate.dto.UsuarioDTO;

public interface UserManagementUseCase {
    UsuarioDTO register(RegisterRequest request);
    UsuarioDTO login(AuthUserDTO request);
    UsuarioDTO getUserById(Long id);
    UsuarioDTO updateUser(Long id, UsuarioDTO userDto);
}
