package com.techmate.techmate.domain.port.out;

import com.techmate.techmate.domain.model.user.Role;

import java.util.List;
import java.util.Optional;

/**
 * 🔌 OUTPUT PORT - RoleRepositoryPort
 * 
 * Define el contrato para persistencia de roles.
 * 
 * @author TechShare Team - Hexagonal Architecture
 * @version 2.0.0
 */
public interface RoleRepositoryPort {

    /**
     * Guarda un nuevo rol o actualiza uno existente
     */
    Role save(Role role);

    /**
     * Busca un rol por su ID
     */
    Optional<Role> findById(Integer id);

    /**
     * Busca un rol por nombre
     */
    Optional<Role> findByName(String name);

    /**
     * Obtiene todos los roles
     */
    List<Role> findAll();

    /**
     * Elimina un rol por su ID
     */
    void deleteById(Integer id);

    /**
     * Verifica si existe un rol por su ID
     */
    boolean existsById(Integer id);

    /**
     * Verifica si existe un rol con el nombre dado
     */
    boolean existsByName(String name);

    /**
     * Obtiene el conteo total de roles
     */
    long count();
}
