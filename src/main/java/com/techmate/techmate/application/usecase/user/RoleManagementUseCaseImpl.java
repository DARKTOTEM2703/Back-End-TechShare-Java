package com.techmate.techmate.application.usecase.user;

import com.techmate.techmate.domain.model.user.Role;
import com.techmate.techmate.domain.port.in.RoleManagementUseCase;
import com.techmate.techmate.domain.port.out.RoleRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 🎯 USE CASE - RoleManagementUseCaseImpl
 * 
 * Implementa la gestión de roles.
 * 
 * @author TechShare Team - Hexagonal Architecture
 * @version 2.0.0
 */
@Service
@Transactional
public class RoleManagementUseCaseImpl implements RoleManagementUseCase {

    private static final Logger log = LoggerFactory.getLogger(RoleManagementUseCaseImpl.class);

    private final RoleRepositoryPort roleRepository;

    public RoleManagementUseCaseImpl(RoleRepositoryPort roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Role> getRoleById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID del rol debe ser válido");
        }
        return roleRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Role> getRoleByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del rol no puede estar vacío");
        }
        return roleRepository.findByName(name.trim());
    }

    @Override
    public Role createRole(CreateRoleRequest request) {
        log.info("🔐 Creando nuevo rol: {}", request.name());

        // Validar que el rol no existe
        if (roleRepository.existsByName(request.name())) {
            throw new IllegalArgumentException(
                String.format("Ya existe un rol con el nombre '%s'", request.name()));
        }

        Role role = Role.builder()
            .name(request.name())
            .build();

        Role saved = roleRepository.save(role);
        log.info("✅ Rol creado con ID: {}", saved.getId());
        return saved;
    }

    @Override
    public void deleteRole(Integer roleId) {
        log.info("🗑️ Eliminando rol: {}", roleId);

        if (!roleRepository.existsById(roleId)) {
            throw new IllegalArgumentException(
                String.format("Rol con ID %d no existe", roleId));
        }

        roleRepository.deleteById(roleId);
        log.info("✅ Rol eliminado: {}", roleId);
    }
}
