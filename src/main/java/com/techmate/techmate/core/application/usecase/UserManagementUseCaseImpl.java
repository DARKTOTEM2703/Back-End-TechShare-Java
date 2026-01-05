package com.techmate.techmate.core.application.usecase.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techmate.techmate.core.domain.model.user.User;
import com.techmate.techmate.infrastructure.persistence.entity.Role;
import com.techmate.techmate.core.application.port.input.UserManagementUseCase;
import com.techmate.techmate.core.application.port.output.UserRepositoryPort;
import com.techmate.techmate.core.application.port.output.RoleRepositoryPort;
import com.techmate.techmate.core.application.port.output.PasswordEncoderPort;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Implementation of user management use cases.
 * 
 * Handles user CRUD operations with validation and business logic.
 * Coordinates with repository and encoder ports.
 */
@Service
@Transactional
public class UserManagementUseCaseImpl implements UserManagementUseCase {

    private static final Logger logger = LoggerFactory.getLogger(UserManagementUseCaseImpl.class);

    private final UserRepositoryPort userRepository;
    private final RoleRepositoryPort roleRepository;
    private final PasswordEncoderPort passwordEncoder;

    public UserManagementUseCaseImpl(
            UserRepositoryPort userRepository,
            RoleRepositoryPort roleRepository,
            PasswordEncoderPort passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ============= QUERIES =============

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Integer id) {
        logger.debug("Getting user by ID: {}", id);
        return userRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByUsername(String username) {
        logger.debug("Getting user by username: {}", username);
        return userRepository.findByUsername(username)
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        logger.debug("Getting user by email: {}", email);
        return userRepository.findByEmail(email)
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        logger.debug("Getting all users");
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getEnabledUsers() {
        logger.debug("Getting all enabled users");
        return userRepository.findEnabledUsers().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getDisabledUsers() {
        logger.debug("Getting all disabled users");
        return userRepository.findDisabledUsers().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByRole(String roleName) {
        logger.debug("Getting users by role: {}", roleName);
        return userRepository.findByRole(roleName).stream()
                .map(this::toResponse)
                .toList();
    }

    // ============= COMMANDS =============

    @Override
    public UserResponse registerUser(RegisterUserRequest request) {
        logger.info("Registering new user: {}", request.username());

        validateRegisterRequest(request);

        // Check if user already exists
        if (userRepository.existsByUsername(request.username())) {
            throw new RuntimeException("Username already exists: " + request.username());
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already exists: " + request.email());
        }

        // Encode password
        String encodedPassword = passwordEncoder.encode(request.plainPassword());

        // Create user domain object
        User newUser = User.builder()
                .username(request.username())
                .email(request.email())
                .passwordHash(encodedPassword)
                .firstName(request.firstName())
                .lastName(request.lastName())
                .birthDate(request.birthDate())
                .gender(request.gender())
                .enabled(false)  // New users start disabled
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .roleNames(Set.of("USER"))  // Default role
                .build();

        User savedUser = userRepository.save(newUser);
        logger.info("User registered successfully: {}", savedUser.getUsername());
        return toResponse(savedUser);
    }

    @Override
    public UserResponse updateUserProfile(UpdateUserProfileRequest request) {
        logger.info("Updating user profile: {}", request.userId());

        User existingUser = userRepository.findById(request.userId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + request.userId()));

        // Create updated user
        User updatedUser = User.builder()
                .id(existingUser.getId())
                .username(existingUser.getUsername())
                .email(existingUser.getEmail())
                .passwordHash(existingUser.getPasswordHash())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .birthDate(request.birthDate())
                .gender(request.gender())
                .enabled(existingUser.isEnabled())
                .profileImageUrl(request.profileImageUrl())
                .createdAt(existingUser.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .roleNames(existingUser.getRoleNames())
                .build();

        User saved = userRepository.save(updatedUser);
        logger.info("User profile updated: {}", saved.getUsername());
        return toResponse(saved);
    }

    @Override
    public void enableUser(Integer userId) {
        logger.info("Enabling user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        if (user.isEnabled()) {
            logger.warn("User is already enabled: {}", userId);
            return;
        }

        User enabledUser = User.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .passwordHash(user.getPasswordHash())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .birthDate(user.getBirthDate())
                .gender(user.getGender())
                .enabled(true)
                .profileImageUrl(user.getProfileImageUrl())
                .createdAt(user.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .roleNames(user.getRoleNames())
                .build();

        userRepository.save(enabledUser);
        logger.info("User enabled successfully: {}", userId);
    }

    @Override
    public void disableUser(Integer userId) {
        logger.info("Disabling user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        if (!user.isEnabled()) {
            logger.warn("User is already disabled: {}", userId);
            return;
        }

        User disabledUser = User.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .passwordHash(user.getPasswordHash())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .birthDate(user.getBirthDate())
                .gender(user.getGender())
                .enabled(false)
                .profileImageUrl(user.getProfileImageUrl())
                .createdAt(user.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .roleNames(user.getRoleNames())
                .build();

        userRepository.save(disabledUser);
        logger.info("User disabled successfully: {}", userId);
    }

    @Override
    public void assignRoleToUser(Integer userId, Integer roleId) {
        logger.info("Assigning role {} to user: {}", roleId, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        com.techmate.techmate.core.domain.model.user.Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with ID: " + roleId));

        Set<String> newRoles = new java.util.HashSet<>(user.getRoleNames());
        newRoles.add(role.getName());

        User updatedUser = User.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .passwordHash(user.getPasswordHash())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .birthDate(user.getBirthDate())
                .gender(user.getGender())
                .enabled(user.isEnabled())
                .profileImageUrl(user.getProfileImageUrl())
                .createdAt(user.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .roleNames(newRoles)
                .build();

        userRepository.save(updatedUser);
        logger.info("Role assigned successfully to user: {}", userId);
    }

    @Override
    public void deleteUser(Integer userId) {
        logger.info("Deleting user: {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with ID: " + userId);
        }

        userRepository.delete(userId);
        logger.info("User deleted successfully: {}", userId);
    }

    // ============= HELPERS =============

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getBirthDate(),
                user.getGender(),
                user.isEnabled(),
                user.getProfileImageUrl(),
                user.getRoleNames()
        );
    }

    private void validateRegisterRequest(RegisterUserRequest request) {
        if (request.username() == null || request.username().trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (request.email() == null || request.email().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (request.plainPassword() == null || request.plainPassword().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (request.firstName() == null || request.firstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (request.lastName() == null || request.lastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }
    }
}
