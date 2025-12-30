package com.techmate.techmate.hexagonal.infrastructure.service.User;

import java.util.Optional;

import com.techmate.techmate.hexagonal.infrastructure.dto.UsuarioDTO;
public interface UserDemoService {
Optional<UsuarioDTO> getUserDetailsFromToken(String token);
}




