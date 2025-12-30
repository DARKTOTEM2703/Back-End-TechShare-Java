package com.techmate.techmate.application.usecase.user;

import com.techmate.techmate.domain.model.user.User;
import com.techmate.techmate.domain.model.user.User.Gender;
import com.techmate.techmate.domain.port.in.UserManagementUseCase;
import com.techmate.techmate.domain.port.out.UserRepositoryPort;
import com.techmate.techmate.domain.port.out.RoleRepositoryPort;
import com.techmate.techmate.domain.port.out.PasswordEncoderPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 🎯 USE CASE - UserManagementUseCaseImpl
 * 
 * Implementa la gestión de usuarios.
 * Orquesta la lógica de negocio usando los ports.
 * 
 * @author TechShare Team - Hexagonal Architecture
 * @version 2.0.0
 */
@Service
@Transactional
public class UserManagementUseCaseImpl implements UserManagementUseCase {

    private static final Logger log = LoggerFactory.getLogger(UserManagementUseCaseImpl.class);

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

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID del usuario debe ser válido");
        }
        return userRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario no puede estar vacío");
        }
        return userRepository.findByUsername(username.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("El email no puede estar vacío");
        }
        return userRepository.findByEmail(email.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getEnabledUsers() {
        return userRepository.findEnabledUsers();
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getDisabledUsers() {
        return userRepository.findDisabledUsers();
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUsersByRole(String roleName) {
        if (roleName == null || roleName.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del rol no puede estar vacío");
        }
        return userRepository.findByRole(roleName.trim());
    }

    @Override
    public User registerUser(RegisterUserRequest request) {
        log.info("👤 Registrando nuevo usuario: {}", request.username());

        // Validar que el usuario no existe
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException(
                String.format("El usuario '%s' ya existe", request.username()));
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException(
                String.format("El email '%s' ya está registrado", request.email()));
        }

        // Crear usuario con rol USER por defecto
        Set<String> defaultRoles = new HashSet<>();
        defaultRoles.add("USER");

        User user = User.builder()
            .username(request.username())
            .email(request.email())
            .firstName(request.firstName())
            .lastName(request.lastName())
            .birthDate(request.birthDate())
            .enabled(false) // Requiere activación
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .roleNames(defaultRoles)
            // passwordHash será establecido por AuthenticationUseCase
            .passwordHash("") // Placeholder - será asignado en auth
            .build();

        User saved = userRepository.save(user);
        log.info("✅ Usuario registrado: {}", saved.getId());
        return saved;
    }

    @Override
    public User updateUserProfile(Integer userId, UpdateUserProfileRequest request) {
        log.info("🔄 Actualizando perfil del usuario: {}", userId);

        User existing = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException(
                String.format("Usuario con ID %d no existe", userId)));

        Gender gender = null;
        if (request.gender() != null && !request.gender().trim().isEmpty()) {
            try {
                gender = Gender.valueOf(request.gender().toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Género inválido: {}", request.gender());
            }
        }

        User updated = User.builder()
            .id(existing.getId())
            .username(existing.getUsername())
            .firstName(request.firstName())
            .lastName(request.lastName())
            .email(existing.getEmail())
            .passwordHash(existing.getPasswordHash())
            .birthDate(request.birthDate())
            .gender(gender)
            .enabled(existing.isEnabled())
            .profileImageUrl(request.profileImageUrl())
            .createdAt(existing.getCreatedAt())
            .updatedAt(LocalDateTime.now())
            .roleNames(existing.getRoleNames())
            .build();

        User saved = userRepository.save(updated);
        log.info("✅ Perfil actualizado: {}", userId);
        return saved;
    }

    @Override
    public User enableUser(Integer userId) {
        log.info("✅ Habilitando usuario: {}", userId);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException(
                String.format("Usuario con ID %d no existe", userId)));

        User enabled = user.enable();
        User saved = userRepository.save(enabled);
        log.info("✅ Usuario habilitado: {}", userId);
        return saved;
    }

    @Override
    public User disableUser(Integer userId) {
        log.info("❌ Deshabilitando usuario: {}", userId);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException(
                String.format("Usuario con ID %d no existe", userId)));

        User disabled = user.disable();
        User saved = userRepository.save(disabled);
        log.info("✅ Usuario deshabilitado: {}", userId);
        return saved;
    }

    @Override
    public User assignRoleToUser(Integer userId, String roleName) {
        log.info("🔐 Asignando rol '{}' al usuario: {}", roleName, userId);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException(
                String.format("Usuario con ID %d no existe", userId)));

        // Validar que el rol existe
        if (!roleRepository.existsByName(roleName)) {
            throw new IllegalArgumentException(
                String.format("El rol '%s' no existe", roleName));
        }

        // Crear nuevo set de roles e incluir el nuevo
        Set<String> updatedRoles = new HashSet<>(user.getRoleNames());
        updatedRoles.add(roleName);

        User updated = User.builder()
            .id(user.getId())
            .username(user.getUsername())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .email(user.getEmail())
            .passwordHash(user.getPasswordHash())
            .birthDate(user.getBirthDate())
            .gender(user.getGender())
            .enabled(user.isEnabled())
            .profileImageUrl(user.getProfileImageUrl())
            .createdAt(user.getCreatedAt())
            .updatedAt(LocalDateTime.now())
            .roleNames(updatedRoles)
            .build();

        User saved = userRepository.save(updated);
        log.info("✅ Rol asignado: {}", userId);
        return saved;
    }

    @Override
    public void deleteUser(Integer userId) {
        log.info("🗑️ Eliminando usuario: {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException(
                String.format("Usuario con ID %d no existe", userId));
        }

        userRepository.deleteById(userId);
        log.info("✅ Usuario eliminado: {}", userId);
    }
}
