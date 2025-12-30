package com.techmate.techmate.hexagonal.infrastructure.service.User.mapper;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.techmate.techmate.hexagonal.infrastructure.dto.UsuarioDTO;
import com.techmate.techmate.hexagonal.domain.entity.Usuario;

@Component
public class UserMapper {

    public UsuarioDTO toDTO(Usuario usuario, Set<String> roles) {
        if (usuario == null) return null;
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setUserName(usuario.getUser_name());
        dto.setFirstName(usuario.getFirst_name());
        dto.setLastName(usuario.getLast_name());
        dto.setEmail(usuario.getEmail());
        dto.setProfileImageUrl(usuario.getProfile_image_url());
        dto.setEnabled(usuario.isEnabled());
        dto.setBirthDate(usuario.getBirthDate());
        dto.setGender(usuario.getGender() != null ? usuario.getGender().toString() : null);
        dto.setCreatedAt(usuario.getCreated_at());
        dto.setUpdatedAt(usuario.getUpdated_at());
        dto.setRoles(roles);
        return dto;
    }
}








