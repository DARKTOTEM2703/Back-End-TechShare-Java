package com.techmate.techmate.service.User;

import java.util.Optional;

import com.techmate.techmate.dto.UsuarioDTO;
public interface UserDemoService {
Optional<UsuarioDTO> getUserDetailsFromToken(String token);
}


