package com.techmate.techmate.domain.port.out;

import com.techmate.techmate.domain.model.user.User;

import java.util.List;
import java.util.Optional;

/**
 * 🔌 OUTPUT PORT - UserRepositoryPort
 * 
 * Define el contrato para persistencia de usuarios.
 * 
 * @author TechShare Team - Hexagonal Architecture
 * @version 2.0.0
 */
public interface UserRepositoryPort {

    /**
     * Guarda un nuevo usuario o actualiza uno existente
     */
    User save(User user);

    /**
     * Busca un usuario por su ID
     */
    Optional<User> findById(Integer id);

    /**
     * Busca un usuario por nombre de usuario
     */
    Optional<User> findByUsername(String username);

    /**
     * Busca un usuario por email
     */
    Optional<User> findByEmail(String email);

    /**
     * Obtiene todos los usuarios
     */
    List<User> findAll();

    /**
     * Obtiene usuarios habilitados
     */
    List<User> findEnabledUsers();

    /**
     * Obtiene usuarios deshabilitados
     */
    List<User> findDisabledUsers();

    /**
     * Busca usuarios por rol
     */
    List<User> findByRole(String roleName);

    /**
     * Elimina un usuario por su ID
     */
    void deleteById(Integer id);

    /**
     * Verifica si existe un usuario por su ID
     */
    boolean existsById(Integer id);

    /**
     * Verifica si existe un usuario con el nombre de usuario dado
     */
    boolean existsByUsername(String username);

    /**
     * Verifica si existe un usuario con el email dado
     */
    boolean existsByEmail(String email);

    /**
     * Obtiene el conteo total de usuarios
     */
    long count();
}
