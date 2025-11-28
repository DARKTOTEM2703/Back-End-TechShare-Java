package com.techmate.techmate.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;

import com.techmate.techmate.dto.RoleDTO;
import com.techmate.techmate.entity.Role;
import com.techmate.techmate.repository.RoleRepository;
import com.techmate.techmate.service.RoleService;

import jakarta.transaction.Transactional;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final com.techmate.techmate.service.role.mapper.RoleMapper roleMapper;
    private final com.techmate.techmate.service.role.validator.RoleValidator roleValidator;
    private final com.techmate.techmate.service.role.query.RoleQueryService roleQueryService;
    private final com.techmate.techmate.service.role.manager.RoleAssociationManager roleAssociationManager;

    public RoleServiceImpl(RoleRepository roleRepository,
            com.techmate.techmate.service.role.mapper.RoleMapper roleMapper,
            com.techmate.techmate.service.role.validator.RoleValidator roleValidator,
            com.techmate.techmate.service.role.query.RoleQueryService roleQueryService,
            com.techmate.techmate.service.role.manager.RoleAssociationManager roleAssociationManager) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
        this.roleValidator = roleValidator;
        this.roleQueryService = roleQueryService;
        this.roleAssociationManager = roleAssociationManager;
    }

    private Role convertToEntity(RoleDTO roleDTO) {
        return roleMapper.toEntity(roleDTO);
    }

    @Override
    public RoleDTO createRole(RoleDTO roleDTO) {

        roleValidator.validateUniqueName(roleDTO.getName());
        Role rol = convertToEntity(roleDTO);
        rol = roleRepository.save(rol);
        return roleMapper.toDTO(rol);
    }

    @Override
    public RoleDTO getRoleById(int roleId) {
        return roleQueryService.getById(roleId);
    }

    @Override
    public RoleDTO updateRole(int roleId, RoleDTO roleDTO) {
        Role rol = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with ID: " + roleId));
        roleValidator.validateUniqueName(roleDTO.getName());
        rol.setNombre(roleDTO.getName());
        Role updatedRole = roleRepository.save(rol);
        return roleMapper.toDTO(updatedRole);
    }

    @Override
    public List<RoleDTO> getAllRole() {
        return roleQueryService.getAllRoles();
    }

    public String getRoleNameById(int roleId) {

        Role rol = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with ID: " + roleId));

        return rol != null ? rol.getNombre() : null;
    }

    @Override
    @Transactional
    public void cleanupRoleAssociations(int roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role no encontrado con id: " + roleId));
        roleAssociationManager.cleanupRoleAssociations(role);
        roleRepository.delete(role);
    }

}
