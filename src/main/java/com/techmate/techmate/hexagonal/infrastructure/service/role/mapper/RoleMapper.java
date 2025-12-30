package com.techmate.techmate.hexagonal.infrastructure.service.role.mapper;

import org.springframework.stereotype.Component;

import com.techmate.techmate.hexagonal.infrastructure.dto.RoleDTO;
import com.techmate.techmate.hexagonal.domain.entity.Role;

@Component
public class RoleMapper {

    public RoleDTO toDTO(Role role) {
        if (role == null) return null;
        RoleDTO dto = new RoleDTO();
        dto.setId(role.getId());
        dto.setName(role.getNombre());
        return dto;
    }

    public Role toEntity(RoleDTO dto) {
        if (dto == null) return null;
        Role role = new Role();
        role.setId(dto.getId());
        role.setNombre(dto.getName());
        return role;
    }
}








