package com.techmate.techmate.infrastructure.service.mapper;

import org.springframework.stereotype.Component;

import com.techmate.techmate.infrastructure.dto.RegisterRequest;
import com.techmate.techmate.domain.entity.Usuario;

@Component
public class AuthMapper {

    public Usuario toEntity(RegisterRequest req) {
        Usuario u = new Usuario();
        u.setUser_name(req.getUser_name());
        u.setFirst_name(req.getFirst_name());
        u.setLast_name(req.getLast_name());
        u.setEmail(req.getEmail());
        u.setPassword(req.getPassword()); // Caller should encode
        u.setBirthDate(req.getBirthDate()); // Fecha de nacimiento
        u.setGender(req.getGender()); // Género
        // roles are handled by service if needed
        return u;
    }
}









