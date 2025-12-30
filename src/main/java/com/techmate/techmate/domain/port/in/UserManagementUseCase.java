package com.techmate.techmate.domain.port.in;

import com.techmate.techmate.domain.model.user.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 🎯 INPUT PORT - UserManagementUseCase
 * 
 * Caso de uso para gestión de usuarios.
 * Define operaciones de consulta y comando sobre usuarios.
 * 
 * @author TechShare Team - Hexagonal Architecture
 * @version 2.0.0
 */
public interface UserManagementUseCase {

    // ═══ QUERIES ═══
    
    /**
     * Obtiene todos los usuarios
     */
    List<User> getAllUsers();

    /**
     * Busca un usuario por su ID
     */
    Optional<User> getUserById(Integer id);

    /**
     * Busca un usuario por nombre de usuario
     */
    Optional<User> getUserByUsername(String username);

    /**
     * Busca un usuario por email
     */
    Optional<User> getUserByEmail(String email);

    /**
     * Obtiene usuarios habilitados
     */
    List<User> getEnabledUsers();

    /**
     * Obtiene usuarios deshabilitados
     */
    List<User> getDisabledUsers();

    /**
     * Busca usuarios por rol
     */
    List<User> getUsersByRole(String roleName);

    // ═══ COMMANDS ═══

    /**
     * Registra un nuevo usuario (operación sin contraseña - la maneja AuthService)
     */
    User registerUser(RegisterUserRequest request);

    /**
     * Actualiza el perfil de un usuario (excepto contraseña)
     */
    User updateUserProfile(Integer userId, UpdateUserProfileRequest request);

    /**
     * Habilita un usuario
     */
    User enableUser(Integer userId);

    /**
     * Deshabilita un usuario
     */
    User disableUser(Integer userId);

    /**
     * Asigna un rol a un usuario
     */
    User assignRoleToUser(Integer userId, String roleName);

    /**
     * Elimina un usuario
     */
    void deleteUser(Integer userId);

    // ═══ DTOs ═══

    record RegisterUserRequest(
        String username,
        String email,
        String firstName,
        String lastName,
        LocalDate birthDate
    ) {
        public RegisterUserRequest {
            if (username == null || username.trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre de usuario es obligatorio");
            }
            if (email == null || email.trim().isEmpty()) {
                throw new IllegalArgumentException("El email es obligatorio");
            }
            if (firstName == null || firstName.trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre es obligatorio");
            }
            if (lastName == null || lastName.trim().isEmpty()) {
                throw new IllegalArgumentException("El apellido es obligatorio");
            }
        }
    }

    record UpdateUserProfileRequest(
        String firstName,
        String lastName,
        LocalDate birthDate,
        String gender,
        String profileImageUrl
    ) {
        public UpdateUserProfileRequest {
            if (firstName == null || firstName.trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre no puede estar vacío");
            }
            if (lastName == null || lastName.trim().isEmpty()) {
                throw new IllegalArgumentException("El apellido no puede estar vacío");
            }
        }
    }
}
