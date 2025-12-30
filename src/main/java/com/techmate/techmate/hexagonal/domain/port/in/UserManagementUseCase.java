package com.techmate.techmate.hexagonal.domain.port.in;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * Input port for user management use cases.
 * 
 * Defines operations for user CRUD and profile management.
 * Commands and queries separated following CQRS pattern.
 */
public interface UserManagementUseCase {

    // ============= QUERIES =============

    /**
     * Get user by ID.
     */
    UserResponse getUserById(Integer id);

    /**
     * Get user by username.
     */
    UserResponse getUserByUsername(String username);

    /**
     * Get user by email.
     */
    UserResponse getUserByEmail(String email);

    /**
     * Get all users.
     */
    List<UserResponse> getAllUsers();

    /**
     * Get all enabled users.
     */
    List<UserResponse> getEnabledUsers();

    /**
     * Get all disabled users.
     */
    List<UserResponse> getDisabledUsers();

    /**
     * Get users with specific role.
     */
    List<UserResponse> getUsersByRole(String roleName);

    // ============= COMMANDS =============

    /**
     * Register a new user.
     */
    UserResponse registerUser(RegisterUserRequest request);

    /**
     * Update user profile.
     */
    UserResponse updateUserProfile(UpdateUserProfileRequest request);

    /**
     * Enable user account.
     */
    void enableUser(Integer userId);

    /**
     * Disable user account.
     */
    void disableUser(Integer userId);

    /**
     * Assign role to user.
     */
    void assignRoleToUser(Integer userId, Integer roleId);

    /**
     * Delete user account.
     */
    void deleteUser(Integer userId);

    // ============= DTOs =============

    /**
     * User response DTO.
     */
    record UserResponse(
            Integer id,
            String username,
            String firstName,
            String lastName,
            String email,
            LocalDate birthDate,
            String gender,
            boolean enabled,
            String profileImageUrl,
            Set<String> roleNames
    ) {}

    /**
     * User registration request DTO.
     */
    record RegisterUserRequest(
            String username,
            String email,
            String plainPassword,
            String firstName,
            String lastName,
            LocalDate birthDate,
            String gender
    ) {}

    /**
     * User profile update request DTO.
     */
    record UpdateUserProfileRequest(
            Integer userId,
            String firstName,
            String lastName,
            LocalDate birthDate,
            String gender,
            String profileImageUrl
    ) {}
}
