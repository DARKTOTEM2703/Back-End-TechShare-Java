package com.techmate.techmate.hexagonal.domain.port.out;

import com.techmate.techmate.hexagonal.domain.model.user.User;

import java.util.List;
import java.util.Optional;

/**
 * Output port for User persistence operations.
 * 
 * Defines the contract for user repository implementations.
 * Isolates domain logic from infrastructure details.
 */
public interface UserRepositoryPort {

    /**
     * Find user by ID.
     */
    Optional<User> findById(Integer id);

    /**
     * Find user by username.
     */
    Optional<User> findByUsername(String username);

    /**
     * Find user by email.
     */
    Optional<User> findByEmail(String email);

    /**
     * Find all users.
     */
    List<User> findAll();

    /**
     * Find all enabled users.
     */
    List<User> findEnabledUsers();

    /**
     * Find all disabled users.
     */
    List<User> findDisabledUsers();

    /**
     * Find users by role name.
     */
    List<User> findByRole(String roleName);

    /**
     * Find paginated users.
     */
    List<User> findAllPaginated(int pageNumber, int pageSize);

    /**
     * Check if user exists by ID.
     */
    boolean existsById(Integer id);

    /**
     * Check if user exists by username.
     */
    boolean existsByUsername(String username);

    /**
     * Check if user exists by email.
     */
    boolean existsByEmail(String email);

    /**
     * Count total users.
     */
    long count();

    /**
     * Save or update user.
     */
    User save(User user);

    /**
     * Delete user by ID.
     */
    void delete(Integer id);

    /**
     * Update last login time for user.
     */
    void updateLastLoginTime(Integer userId);
}
