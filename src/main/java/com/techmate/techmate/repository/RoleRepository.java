package com.techmate.techmate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.lang.NonNull;

import com.techmate.techmate.entity.Role;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Role with @EntityGraph to prevent N+1 queries.
 * Uses @EntityGraph to eagerly load privileges relationship.
 */
public interface RoleRepository extends JpaRepository<Role, Integer> {
    
    /**
     * Find by name with privileges loaded (prevents N+1).
     */
    @EntityGraph(attributePaths = {"privileges"})
    Optional<Role> findByName(String name);
    
    /**
     * Find by name (case-insensitive) with privileges loaded.
     */
    @EntityGraph(attributePaths = {"privileges"})
    Optional<Role> findByNameIgnoreCase(String name);
    
    /**
     * Find by ID with privileges loaded (prevents N+1).
     * Override to add @EntityGraph.
     */
    @EntityGraph(attributePaths = {"privileges"})
    @NonNull
    Optional<Role> findById(@NonNull Integer id);
    
    /**
     * Find all roles with privileges loaded (prevents N+1).
     * Override to add @EntityGraph.
     */
    @EntityGraph(attributePaths = {"privileges"})
    @NonNull
    List<Role> findAll();
}


