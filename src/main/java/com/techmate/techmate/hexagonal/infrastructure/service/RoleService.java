package com.techmate.techmate.hexagonal.infrastructure.service;

import java.util.*;

import com.techmate.techmate.hexagonal.infrastructure.dto.RoleDTO;

public interface RoleService {
    
    RoleDTO createRole(RoleDTO roleDTO);
    RoleDTO getRoleById(int roleId);
    RoleDTO updateRole(int roleId, RoleDTO roleDTO);
    List<RoleDTO> getAllRole();
    
    void cleanupRoleAssociations(int roleId);

    


    String getRoleNameById(int roleId);
}








