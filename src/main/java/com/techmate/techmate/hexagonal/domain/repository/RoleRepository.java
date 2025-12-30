package com.techmate.techmate.hexagonal.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import com.techmate.techmate.hexagonal.domain.entity.Role;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Role.
 */
public interface RoleRepository extends JpaRepository<Role, Integer> {

    /**
     * Find by name.
     */
    Optional<Role> findByName(String name);

    /**
     * Find by name (case-insensitive).
     */
    Optional<Role> findByNameIgnoreCase(String name);

    /**
     * Find by ID.
     */
    @NonNull
    Optional<Role> findById(@NonNull Integer id);

    /**
     * Find all roles.
     */
    @NonNull
    List<Role> findAll();
}







