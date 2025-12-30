package com.techmate.techmate.infrastructure.service.User;

import java.util.Optional;

import com.techmate.techmate.infrastructure.dto.UsuarioDTO;
public interface UserDemoService {
Optional<UsuarioDTO> getUserDetailsFromToken(String token);
}









