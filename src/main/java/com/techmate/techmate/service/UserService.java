package com.techmate.techmate.service;

import java.util.List;
import java.util.Optional;

import com.techmate.techmate.dto.UsuarioDTO;

public interface UserService {
    List<UsuarioDTO> getAllUser();
    
    Optional<UsuarioDTO> findUserById(Integer id);
    
    void deleteUsuser(Integer id);
    
    // Agregado método de actualización
    Optional<UsuarioDTO> updateUser(Integer id, UsuarioDTO usuarioDTO);
}


