package com.techmate.techmate.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;

import com.techmate.techmate.dto.RoleDTO;
import com.techmate.techmate.entity.Role;
import com.techmate.techmate.service.RoleService;
import com.techmate.techmate.service.role.manager.RoleManager;
import com.techmate.techmate.service.role.mapper.RoleMapper;
import com.techmate.techmate.service.role.query.RoleQueryService;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleManager roleManager;
    private final RoleQueryService roleQueryService;
    private final RoleMapper roleMapper;

    public RoleServiceImpl(RoleManager roleManager,
                           RoleQueryService roleQueryService,
                           RoleMapper roleMapper) {
        this.roleManager = roleManager;
        this.roleQueryService = roleQueryService;
        this.roleMapper = roleMapper;
    }

    @Override
    public RoleDTO createRole(RoleDTO roleDTO) {
        Role createdRole = roleManager.createRole(roleDTO);
        return roleMapper.toDTO(createdRole);
    }

    @Override
    public RoleDTO getRoleById(int roleId) {
        return roleQueryService.getById(roleId);
    }

    @Override
    public RoleDTO updateRole(int roleId, RoleDTO roleDTO) {
        Role updatedRole = roleManager.updateRole(roleId, roleDTO);
        return roleMapper.toDTO(updatedRole);
    }

    @Override
    public List<RoleDTO> getAllRole() {
        return roleQueryService.getAllRoles();
    }

    @Override
    public String getRoleNameById(int roleId) {
        return roleQueryService.getRoleNameById(roleId);
    }

    @Override
    public void cleanupRoleAssociations(int roleId) {
        // La lógica de limpieza y eliminación ahora reside encapsulada en el Manager
        roleManager.deleteRole(roleId);
    }

}
