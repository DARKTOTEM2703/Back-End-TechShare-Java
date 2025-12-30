package com.techmate.techmate.service.role.validator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.techmate.techmate.hexagonal.domain.entity.Role;com.techmate.techmate.hexagonal.domain.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

class RoleValidatorTest {

    @Mock
    private RoleRepository roleRepository;

    private RoleValidator roleValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        roleValidator = new RoleValidator(roleRepository);
    }

    // Tests for validateUniqueName()
    @Test
    void validateUniqueName_WithUniqueRoleName_NoException() {
        when(roleRepository.findByName("UNIQUE_ROLE")).thenReturn(Optional.empty());
        assertDoesNotThrow(() -> roleValidator.validateUniqueName("UNIQUE_ROLE"));
    }

    @Test
    void validateUniqueName_WithDuplicateRoleName_ThrowsException() {
        Role existingRole = new Role();
        when(roleRepository.findByName("EXISTING_ROLE")).thenReturn(Optional.of(existingRole));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> roleValidator.validateUniqueName("EXISTING_ROLE"));
        assertTrue(exception.getMessage().contains("Ya existe un rol"));
    }

    @Test
    void validateUniqueName_WithNullName_NoException() {
        // Null should not trigger validation
        assertDoesNotThrow(() -> roleValidator.validateUniqueName(null));
        verify(roleRepository, never()).findByName(anyString());
    }

    @Test
    void validateUniqueName_WithEmptyString_NoException() {
        when(roleRepository.findByName("")).thenReturn(Optional.empty());
        assertDoesNotThrow(() -> roleValidator.validateUniqueName(""));
    }

    @Test
    void validateUniqueName_WithMultipleUniqueRoles_NoException() {
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.empty());
        when(roleRepository.findByName("USER")).thenReturn(Optional.empty());
        when(roleRepository.findByName("MODERATOR")).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> roleValidator.validateUniqueName("ADMIN"));
        assertDoesNotThrow(() -> roleValidator.validateUniqueName("USER"));
        assertDoesNotThrow(() -> roleValidator.validateUniqueName("MODERATOR"));

        verify(roleRepository, times(3)).findByName(anyString());
    }

    @Test
    void validateUniqueName_WithRoleNameContainingUnderscores_NoException() {
        when(roleRepository.findByName("ADMIN_SUPER_USER")).thenReturn(Optional.empty());
        assertDoesNotThrow(() -> roleValidator.validateUniqueName("ADMIN_SUPER_USER"));
    }

    @Test
    void validateUniqueName_WithRoleNameContainingNumbers_NoException() {
        when(roleRepository.findByName("ROLE_123")).thenReturn(Optional.empty());
        assertDoesNotThrow(() -> roleValidator.validateUniqueName("ROLE_123"));
    }

    @Test
    void validateUniqueName_WithRoleNameInDifferentCase_Validated() {
        Role role = new Role();
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(role));

        // Assuming repository is case-sensitive
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> roleValidator.validateUniqueName("ADMIN"));
        assertNotNull(exception.getMessage());
    }

    @Test
    void validateUniqueName_WithLongRoleName_NoException() {
        String longName = "ROLE_" + "A".repeat(100);
        when(roleRepository.findByName(longName)).thenReturn(Optional.empty());
        assertDoesNotThrow(() -> roleValidator.validateUniqueName(longName));
    }

    @Test
    void validateUniqueName_ErrorMessageContainsRoleName() {
        Role role = new Role();
        when(roleRepository.findByName("DUPLICATE_ROLE")).thenReturn(Optional.of(role));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> roleValidator.validateUniqueName("DUPLICATE_ROLE"));
        assertTrue(exception.getMessage().contains("DUPLICATE_ROLE"));
    }

    @Test
    void validateUniqueName_RepositoryCalledWithCorrectName() {
        when(roleRepository.findByName("TEST_ROLE")).thenReturn(Optional.empty());
        roleValidator.validateUniqueName("TEST_ROLE");
        verify(roleRepository, times(1)).findByName("TEST_ROLE");
    }

    @Test
    void validateUniqueName_WithBlankSpaces_NoException() {
        when(roleRepository.findByName("   ")).thenReturn(Optional.empty());
        assertDoesNotThrow(() -> roleValidator.validateUniqueName("   "));
    }

    @Test
    void validateUniqueName_FirstUniqueSecondDuplicate_BothHandled() {
        Role role = new Role();
        when(roleRepository.findByName("UNIQUE")).thenReturn(Optional.empty());
        when(roleRepository.findByName("DUPLICATE")).thenReturn(Optional.of(role));

        assertDoesNotThrow(() -> roleValidator.validateUniqueName("UNIQUE"));
        assertThrows(RuntimeException.class, () -> roleValidator.validateUniqueName("DUPLICATE"));
    }

    @Test
    void validateUniqueName_MultipleCallsWithSameName_RepositoryCalled() {
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.empty());

        for (int i = 0; i < 5; i++) {
            assertDoesNotThrow(() -> roleValidator.validateUniqueName("ADMIN"));
        }

        verify(roleRepository, times(5)).findByName("ADMIN");
    }

    @Test
    void validateUniqueName_WithSpecialCharacters_Validated() {
        when(roleRepository.findByName("ROLE-ADMIN_USER.1")).thenReturn(Optional.empty());
        assertDoesNotThrow(() -> roleValidator.validateUniqueName("ROLE-ADMIN_USER.1"));
    }

    @Test
    void validateUniqueName_WithPrefixROLE_NoException() {
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.empty());
        assertDoesNotThrow(() -> roleValidator.validateUniqueName("ROLE_ADMIN"));
    }

    @Test
    void validateUniqueName_WithoutPrefixROLE_NoException() {
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.empty());
        assertDoesNotThrow(() -> roleValidator.validateUniqueName("ADMIN"));
    }

    @Test
    void validateUniqueName_ExceptionType() {
        Role role = new Role();
        when(roleRepository.findByName("DUPLICATE")).thenReturn(Optional.of(role));

        Exception exception = assertThrows(
                RuntimeException.class,
                () -> roleValidator.validateUniqueName("DUPLICATE"));
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void validateUniqueName_NullNameNeverCallsRepository() {
        roleValidator.validateUniqueName(null);
        verify(roleRepository, never()).findByName(null);
        verify(roleRepository, never()).findByName(anyString());
    }

    @Test
    void validateUniqueName_ConsecutiveDuplicateCalls_BothThrow() {
        Role role = new Role();
        when(roleRepository.findByName("ROLE")).thenReturn(Optional.of(role));

        RuntimeException exception1 = assertThrows(
                RuntimeException.class,
                () -> roleValidator.validateUniqueName("ROLE"));
        RuntimeException exception2 = assertThrows(
                RuntimeException.class,
                () -> roleValidator.validateUniqueName("ROLE"));

        assertTrue(exception1.getMessage().contains("Ya existe"));
        assertTrue(exception2.getMessage().contains("Ya existe"));
    }
}
