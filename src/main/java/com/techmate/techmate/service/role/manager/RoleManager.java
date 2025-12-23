package com.techmate.techmate.service.role.manager;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.techmate.techmate.dto.RoleDTO;
import com.techmate.techmate.entity.Role;
import com.techmate.techmate.repository.RoleRepository;
import com.techmate.techmate.service.role.mapper.RoleMapper;
import com.techmate.techmate.service.role.validator.RoleValidator;

@Component
public class RoleManager {

    private final RoleRepository roleRepository;
    private final RoleValidator roleValidator;
    private final RoleMapper roleMapper;
    private final RoleAssociationManager roleAssociationManager;

    public RoleManager(RoleRepository roleRepository,
            RoleValidator roleValidator,
            RoleMapper roleMapper,
            RoleAssociationManager roleAssociationManager) {
        this.roleRepository = roleRepository;
        this.roleValidator = roleValidator;
        this.roleMapper = roleMapper;
        this.roleAssociationManager = roleAssociationManager;
    }

    @Transactional
    public Role createRole(RoleDTO roleDTO) {
        // 1. Validar reglas de negocio
        roleValidator.validateUniqueName(roleDTO.getName());

        // 2. Convertir y persistir
        Role role = roleMapper.toEntity(roleDTO);
        return roleRepository.save(role);
    }

    @Transactional
    public Role updateRole(int roleId, RoleDTO roleDTO) {
        // 1. Buscar existente
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with ID: " + roleId));

        // 2. Validar
        // Nota: Podría requerirse una validación que excluya el propio ID si el nombre
        // no cambió,
        // pero mantenemos la lógica original del servicio por fidelidad.
        roleValidator.validateUniqueName(roleDTO.getName());

        // 3. Actualizar estado
        role.setNombre(roleDTO.getName());

        // 4. Persistir
        return roleRepository.save(role);
    }

    @Transactional
    public void deleteRole(int roleId) {
        // 1. Buscar existente
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role no encontrado con id: " + roleId));

        // 2. Limpiar asociaciones (Lógica compleja delegada al manager específico)
        roleAssociationManager.cleanupRoleAssociations(role);

        // 3. Eliminar físicamente
        roleRepository.delete(role);
    }
}
