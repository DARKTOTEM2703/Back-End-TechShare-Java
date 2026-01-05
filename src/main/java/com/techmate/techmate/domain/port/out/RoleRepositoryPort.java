package com.techmate.techmate.domain.port.out;

import com.techmate.techmate.infrastructure.persistence.entity.Role;

import java.util.List;
import java.util.Optional;

/**
 * Output port for Role persistence operations.
 * 
 * Defines the contract for role repository implementations.
 * Isolates domain logic from infrastructure details.
 */
public interface RoleRepositoryPort {

    /**
     * Find role by ID.
     */
    Optional<Role> findById(Integer id);

    /**
     * Find role by name.
     */
    Optional<Role> findByName(String name);

    /**
     * Find all roles.
     */
    List<Role> findAll();

    /**
     * Check if role exists by ID.
     */
    boolean existsById(Integer id);

    /**
     * Check if role exists by name.
     */
    boolean existsByName(String name);

    /**
     * Count total roles.
     */
    long count();

    /**
     * Save or update role.
     */
    Role save(Role role);

    /**
     * Delete role by ID.
     */
    void delete(Integer id);
}
