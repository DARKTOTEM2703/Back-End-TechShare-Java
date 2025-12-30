package com.techmate.techmate.service.User.validator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.techmate.techmate.entity.Role;
import com.techmate.techmate.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

class UserValidatorTest {

    @Mock
    private RoleRepository roleRepository;

    private UserValidator userValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userValidator = new UserValidator(roleRepository);
    }

    // Tests for validateRolesExist()
    @Test
    void validateRolesExist_WithValidRoles_NoException() {
        Role role1 = new Role();
        Role role2 = new Role();
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(role1));
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(role2));

        Set<String> roles = new HashSet<>(Arrays.asList("ADMIN", "USER"));
        assertDoesNotThrow(() -> userValidator.validateRolesExist(roles));
    }

    @Test
    void validateRolesExist_WithNullRoles_NoException() {
        // Null check should pass
        assertDoesNotThrow(() -> userValidator.validateRolesExist(null));
        verify(roleRepository, never()).findByName(anyString());
    }

    @Test
    void validateRolesExist_WithEmptyRoles_NoException() {
        Set<String> roles = new HashSet<>();
        assertDoesNotThrow(() -> userValidator.validateRolesExist(roles));
        verify(roleRepository, never()).findByName(anyString());
    }

    @Test
    void validateRolesExist_WithNonExistentRole_ThrowsException() {
        when(roleRepository.findByName("NONEXISTENT")).thenReturn(Optional.empty());

        Set<String> roles = new HashSet<>(Collections.singletonList("NONEXISTENT"));
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userValidator.validateRolesExist(roles)
        );
        assertTrue(exception.getMessage().contains("Rol no encontrado"));
    }

    @Test
    void validateRolesExist_WithPartiallyValidRoles_ThrowsException() {
        Role role = new Role();
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(role));
        when(roleRepository.findByName("INVALID")).thenReturn(Optional.empty());

        Set<String> roles = new HashSet<>(Arrays.asList("ADMIN", "INVALID"));
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userValidator.validateRolesExist(roles)
        );
        assertTrue(exception.getMessage().contains("INVALID"));
    }

    @Test
    void validateRolesExist_WithMultipleValidRoles_NoException() {
        Role role = new Role();
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(role));
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(role));
        when(roleRepository.findByName("MODERATOR")).thenReturn(Optional.of(role));
        when(roleRepository.findByName("VIEWER")).thenReturn(Optional.of(role));

        Set<String> roles = new HashSet<>(Arrays.asList("ADMIN", "USER", "MODERATOR", "VIEWER"));
        assertDoesNotThrow(() -> userValidator.validateRolesExist(roles));

        verify(roleRepository, times(4)).findByName(anyString());
    }

    @Test
    void validateRolesExist_WithCaseInsensitiveRoles_NoException() {
        Role role = new Role();
        when(roleRepository.findByName("admin")).thenReturn(Optional.of(role));

        Set<String> roles = new HashSet<>(Collections.singletonList("admin"));
        assertDoesNotThrow(() -> userValidator.validateRolesExist(roles));
    }

    @Test
    void validateRolesExist_WithSingleRole_NoException() {
        Role role = new Role();
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(role));

        Set<String> roles = new HashSet<>(Collections.singletonList("USER"));
        assertDoesNotThrow(() -> userValidator.validateRolesExist(roles));
    }

    @Test
    void validateRolesExist_WithSpecialCharacterRoleName_NoException() {
        Role role = new Role();
        when(roleRepository.findByName("ROLE_ADMIN-SUPER")).thenReturn(Optional.of(role));

        Set<String> roles = new HashSet<>(Collections.singletonList("ROLE_ADMIN-SUPER"));
        assertDoesNotThrow(() -> userValidator.validateRolesExist(roles));
    }

    @Test
    void validateRolesExist_WithLongRoleName_NoException() {
        Role role = new Role();
        String longRoleName = "ROLE_" + "A".repeat(100);
        when(roleRepository.findByName(longRoleName)).thenReturn(Optional.of(role));

        Set<String> roles = new HashSet<>(Collections.singletonList(longRoleName));
        assertDoesNotThrow(() -> userValidator.validateRolesExist(roles));
    }

    @Test
    void validateRolesExist_FirstRoleValidSecondInvalid_ThrowsException() {
        Role role = new Role();
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(role));
        when(roleRepository.findByName("INVALID_ROLE")).thenReturn(Optional.empty());

        Set<String> roles = new HashSet<>(Arrays.asList("ADMIN", "INVALID_ROLE"));
        assertThrows(IllegalArgumentException.class, () -> userValidator.validateRolesExist(roles));
    }

    @Test
    void validateRolesExist_WithDuplicateRolesInSet_Handled() {
        // Sets automatically deduplicate, so this test ensures deduplication works
        Role role = new Role();
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(role));

        Set<String> roles = new HashSet<>(Arrays.asList("ADMIN", "ADMIN", "ADMIN"));
        assertDoesNotThrow(() -> userValidator.validateRolesExist(roles));

        // Should only call findByName once due to set deduplication
        verify(roleRepository).findByName("ADMIN");
    }

    @Test
    void validateRolesExist_AllRolesInvalid_ThrowsException() {
        when(roleRepository.findByName("INVALID1")).thenReturn(Optional.empty());
        when(roleRepository.findByName("INVALID2")).thenReturn(Optional.empty());

        Set<String> roles = new HashSet<>(Arrays.asList("INVALID1", "INVALID2"));
        assertThrows(IllegalArgumentException.class, () -> userValidator.validateRolesExist(roles));
    }

    @Test
    void validateRolesExist_ErrorMessageContainsRoleName() {
        when(roleRepository.findByName("MISSING_ROLE")).thenReturn(Optional.empty());

        Set<String> roles = new HashSet<>(Collections.singletonList("MISSING_ROLE"));
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userValidator.validateRolesExist(roles)
        );
        assertTrue(exception.getMessage().contains("MISSING_ROLE"));
    }

    @Test
    void validateRolesExist_RepositoryCalledForEachRole() {
        Role role = new Role();
        when(roleRepository.findByName("ROLE1")).thenReturn(Optional.of(role));
        when(roleRepository.findByName("ROLE2")).thenReturn(Optional.of(role));
        when(roleRepository.findByName("ROLE3")).thenReturn(Optional.of(role));

        Set<String> roles = new HashSet<>(Arrays.asList("ROLE1", "ROLE2", "ROLE3"));
        userValidator.validateRolesExist(roles);

        verify(roleRepository).findByName("ROLE1");
        verify(roleRepository).findByName("ROLE2");
        verify(roleRepository).findByName("ROLE3");
    }

    @Test
    void validateRolesExist_EmptyStringRole_Validated() {
        when(roleRepository.findByName("")).thenReturn(Optional.empty());

        Set<String> roles = new HashSet<>(Collections.singletonList(""));
        assertThrows(IllegalArgumentException.class, () -> userValidator.validateRolesExist(roles));
    }

    @Test
    void validateRolesExist_WhitespaceRole_Validated() {
        when(roleRepository.findByName("   ")).thenReturn(Optional.empty());

        Set<String> roles = new HashSet<>(Collections.singletonList("   "));
        assertThrows(IllegalArgumentException.class, () -> userValidator.validateRolesExist(roles));
    }

    @Test
    void validateRolesExist_LargeRoleSet_AllValidated() {
        Role role = new Role();
        Set<String> roles = new HashSet<>();
        for (int i = 1; i <= 10; i++) {
            String roleName = "ROLE_" + i;
            roles.add(roleName);
            when(roleRepository.findByName(roleName)).thenReturn(Optional.of(role));
        }

        assertDoesNotThrow(() -> userValidator.validateRolesExist(roles));
        verify(roleRepository, times(10)).findByName(anyString());
    }

    @Test
    void validateRolesExist_RoleNameWithNumbers_NoException() {
        Role role = new Role();
        when(roleRepository.findByName("ROLE123")).thenReturn(Optional.of(role));

        Set<String> roles = new HashSet<>(Collections.singletonList("ROLE123"));
        assertDoesNotThrow(() -> userValidator.validateRolesExist(roles));
    }

    @Test
    void validateRolesExist_RoleNameWithUnderscores_NoException() {
        Role role = new Role();
        when(roleRepository.findByName("ADMIN_SUPER_USER")).thenReturn(Optional.of(role));

        Set<String> roles = new HashSet<>(Collections.singletonList("ADMIN_SUPER_USER"));
        assertDoesNotThrow(() -> userValidator.validateRolesExist(roles));
    }
}

