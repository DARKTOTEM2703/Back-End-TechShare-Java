package com.techmate.techmate.application.usecase.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techmate.techmate.domain.model.user.Role;
import com.techmate.techmate.domain.port.in.RoleManagementUseCase;
import com.techmate.techmate.domain.port.out.RoleRepositoryPort;

import java.util.List;

/**
 * Implementation of role management use cases.
 * 
 * Handles role CRUD operations.
 * Coordinates with role repository port.
 */
@Service
@Transactional
public class RoleManagementUseCaseImpl implements RoleManagementUseCase {

    private static final Logger logger = LoggerFactory.getLogger(RoleManagementUseCaseImpl.class);

    private final RoleRepositoryPort roleRepository;

    public RoleManagementUseCaseImpl(RoleRepositoryPort roleRepository) {
        this.roleRepository = roleRepository;
    }

    // ============= QUERIES =============

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRoles() {
        logger.debug("Getting all roles");
        return roleRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponse getRoleById(Integer id) {
        logger.debug("Getting role by ID: {}", id);
        return roleRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Role not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponse getRoleByName(String name) {
        logger.debug("Getting role by name: {}", name);
        return roleRepository.findByName(name)
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Role not found with name: " + name));
    }

    // ============= COMMANDS =============

    @Override
    public RoleResponse createRole(CreateRoleRequest request) {
        logger.info("Creating new role: {}", request.name());

        // Check if role already exists
        if (roleRepository.existsByName(request.name())) {
            throw new RuntimeException("Role already exists: " + request.name());
        }

        // Create role domain object
        Role newRole = Role.builder()
                .name(request.name().toUpperCase())
                .build();

        Role savedRole = roleRepository.save(newRole);
        logger.info("Role created successfully: {}", savedRole.getName());
        return toResponse(savedRole);
    }

    @Override
    public void deleteRole(Integer id) {
        logger.info("Deleting role: {}", id);

        if (!roleRepository.existsById(id)) {
            throw new RuntimeException("Role not found with ID: " + id);
        }

        roleRepository.delete(id);
        logger.info("Role deleted successfully: {}", id);
    }

    // ============= HELPERS =============

    private RoleResponse toResponse(Role role) {
        return new RoleResponse(
                role.getId(),
                role.getName()
        );
    }
}
