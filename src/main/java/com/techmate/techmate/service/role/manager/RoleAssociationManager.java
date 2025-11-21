package com.techmate.techmate.service.role.manager;

import java.util.List;

import org.springframework.stereotype.Component;

import com.techmate.techmate.entity.Materials;
import com.techmate.techmate.entity.Role;
import com.techmate.techmate.entity.RoleMaterials;
import com.techmate.techmate.entity.UsuarioRole;
import com.techmate.techmate.repository.RoleMaterialsRepository;
import com.techmate.techmate.repository.UsuarioRoleRepository;

@Component
public class RoleAssociationManager {

    private final RoleMaterialsRepository roleMaterialsRepository;
    private final UsuarioRoleRepository usuarioRoleRepository;

    public RoleAssociationManager(RoleMaterialsRepository roleMaterialsRepository, UsuarioRoleRepository usuarioRoleRepository) {
        this.roleMaterialsRepository = roleMaterialsRepository;
        this.usuarioRoleRepository = usuarioRoleRepository;
    }

    public void cleanupRoleAssociations(Role role) {
        List<RoleMaterials> roleMaterialsList = roleMaterialsRepository.findByRole(role);
        for (RoleMaterials roleMaterials : roleMaterialsList) {
            Materials material = roleMaterials.getMaterials();
            material.getRoleMaterials().remove(roleMaterials);
            roleMaterials.setRole(null);
        }
        roleMaterialsRepository.deleteAll(roleMaterialsList);

        List<UsuarioRole> usuarioRoles = usuarioRoleRepository.findByRole(role);
        for (UsuarioRole usuarioRole : usuarioRoles) {
            usuarioRole.getUsuario().getRoles().remove(role);
        }
        usuarioRoleRepository.deleteAll(usuarioRoles);
    }
}


