package com.techmate.techmate.domain.port.in;

import com.techmate.techmate.domain.model.user.Role;

import java.util.List;
import java.util.Optional;

/**
 * 🎯 INPUT PORT - RoleManagementUseCase
 * 
 * Caso de uso para gestión de roles.
 * 
 * @author TechShare Team - Hexagonal Architecture
 * @version 2.0.0
 */
public interface RoleManagementUseCase {

    // ═══ QUERIES ═══
    
    /**
     * Obtiene todos los roles
     */
    List<Role> getAllRoles();

    /**
     * Busca un rol por su ID
     */
    Optional<Role> getRoleById(Integer id);

    /**
     * Busca un rol por nombre
     */
    Optional<Role> getRoleByName(String name);

    // ═══ COMMANDS ═══

    /**
     * Crea un nuevo rol
     */
    Role createRole(CreateRoleRequest request);

    /**
     * Elimina un rol
     */
    void deleteRole(Integer roleId);

    // ═══ DTOs ═══

    record CreateRoleRequest(
        String name
    ) {
        public CreateRoleRequest {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre del rol es obligatorio");
            }
            if (name.length() > 50) {
                throw new IllegalArgumentException("El nombre del rol no puede tener más de 50 caracteres");
            }
        }
    }
}
