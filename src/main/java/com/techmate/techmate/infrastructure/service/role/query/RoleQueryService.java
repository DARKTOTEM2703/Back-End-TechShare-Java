package com.techmate.techmate.infrastructure.service.role.query;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.techmate.techmate.infrastructure.dto.RoleDTO;
import com.techmate.techmate.infrastructure.persistence.entity.Role;
import com.techmate.techmate.infrastructure.persistence.repository.RoleRepository;
import com.techmate.techmate.infrastructure.service.role.mapper.RoleMapper;

@Component
@Transactional(readOnly = true)
public class RoleQueryService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public RoleQueryService(RoleRepository roleRepository, RoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
    }

    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll().stream()
                .filter(r -> r.getId() != 1) // Filtro de negocio existente (excluir rol ID 1)
                .map(roleMapper::toDTO)
                .collect(Collectors.toList());
    }

    public RoleDTO getById(int id) {
        return roleRepository.findById(id)
                .map(roleMapper::toDTO)
                .orElse(null); // Contrato original retorna null si no existe
    }

    public String getRoleNameById(int id) {
        return roleRepository.findById(id)
                .map(Role::getNombre)
                .orElseThrow(() -> new RuntimeException("Role not found with ID: " + id));
    }
}







