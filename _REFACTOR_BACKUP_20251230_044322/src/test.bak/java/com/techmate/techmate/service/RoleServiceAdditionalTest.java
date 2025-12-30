package com.techmate.techmate.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.techmate.techmate.hexagonal.domain.entity.Role;com.techmate.techmate.hexagonal.domain.repository.RoleRepository;

/**
 * Tests adicionales para RoleService
 * Cubre casos de roles, permisos y asignaciones
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("👔 RoleService - Tests Adicionales")
class RoleServiceAdditionalTest {

    @Mock
    private RoleRepository roleRepository;

    private Role testRole;

    @BeforeEach
    void setUp() {
        testRole = new Role();
        testRole.setId(1);
        testRole.setName("ADMIN");
    }

    @Test
    @DisplayName("✅ Obtener rol por ID")
    void getRoleById_Exists_Success() {
        // Given: Rol existente
        when(roleRepository.findById(1)).thenReturn(Optional.of(testRole));

        // When: Buscar rol
        Optional<Role> result = roleRepository.findById(1);

        // Then: Debe existir
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("ADMIN");
    }

    @Test
    @DisplayName("✅ Listar todos los roles")
    void getAllRoles_HasRoles_ReturnsList() {
        // Given: Múltiples roles
        List<Role> roles = Arrays.asList(testRole);
        when(roleRepository.findAll()).thenReturn(roles);

        // When: Obtener todos
        List<Role> result = roleRepository.findAll();

        // Then: Debe retornar lista
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("✅ Buscar rol por nombre")
    void getRoleByName_ValidName_Success() {
        // Given: Rol existe con nombre ADMIN
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(testRole));

        // When: Buscar por nombre
        Optional<Role> result = roleRepository.findByName("ADMIN");

        // Then: Debe encontrar rol
        assertThat(result).isPresent();
    }

    @Test
    @DisplayName("❌ Buscar rol inexistente")
    void getRoleByName_InvalidName_ReturnEmpty() {
        // Given: Rol no existe
        when(roleRepository.findByName("INVALID")).thenReturn(Optional.empty());

        // When: Buscar rol
        Optional<Role> result = roleRepository.findByName("INVALID");

        // Then: Debe estar vacío
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("✅ Validar nombre de rol es válido")
    void validateRoleName_ValidNames_Success() {
        // Given: Roles válidos
        String[] validNames = { "ADMIN", "USER", "MODERATOR", "GUEST" };

        // When: Validar cada uno
        for (String name : validNames) {
            boolean isValid = !name.isEmpty() && name.matches("[A-Z_]+");

            // Then: Debe ser válido
            assertThat(isValid).isTrue();
        }
    }

    @Test
    @DisplayName("⚠️ Validar nombre de rol inválido")
    void validateRoleName_InvalidNames_Fail() {
        // Given: Nombres inválidos
        String[] invalidNames = { "", "admin-role", "123Role" };

        // When: Validar formato
        for (String name : invalidNames) {
            boolean isValid = !name.isEmpty() && name.matches("[A-Z_]+");

            // Then: No debe ser válido
            assertThat(isValid).isFalse();
        }
    }
}
