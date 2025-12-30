package com.techmate.techmate.hexagonal.domain.port.in;

import java.util.List;

/**
 * Input port for role management use cases.
 * 
 * Defines operations for role CRUD operations.
 */
public interface RoleManagementUseCase {

    // ============= QUERIES =============

    /**
     * Get all roles.
     */
    List<RoleResponse> getAllRoles();

    /**
     * Get role by ID.
     */
    RoleResponse getRoleById(Integer id);

    /**
     * Get role by name.
     */
    RoleResponse getRoleByName(String name);

    // ============= COMMANDS =============

    /**
     * Create a new role.
     */
    RoleResponse createRole(CreateRoleRequest request);

    /**
     * Delete role by ID.
     */
    void deleteRole(Integer id);

    // ============= DTOs =============

    /**
     * Role response DTO.
     */
    record RoleResponse(
            Integer id,
            String name
    ) {}

    /**
     * Create role request DTO.
     */
    record CreateRoleRequest(
            String name
    ) {}
}
