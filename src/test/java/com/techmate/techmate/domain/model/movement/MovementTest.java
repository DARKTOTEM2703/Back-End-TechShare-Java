package com.techmate.techmate.domain.model.movement;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

@DisplayName("Role Domain Model Tests")
class MovementTest {

    @DisplayName("TEST 1: Should create Role using Builder")
    @Test
    void testRoleCreation() {
        // Using Role as the movement example for now
        com.techmate.techmate.domain.model.user.Role role = com.techmate.techmate.domain.model.user.Role.builder()
                .id(1)
                .name("ADMIN")
                .build();

        assertNotNull(role);
        assertEquals("ADMIN", role.getName());
    }

    @DisplayName("TEST 2: Should return correct role id")
    @Test
    void testGetRoleId() {
        com.techmate.techmate.domain.model.user.Role role = com.techmate.techmate.domain.model.user.Role.builder()
                .id(42)
                .name("ADMIN")
                .build();

        assertEquals(42, role.getId());
    }

    @DisplayName("TEST 3: Should return correct role name")
    @Test
    void testGetRoleName() {
        com.techmate.techmate.domain.model.user.Role role = com.techmate.techmate.domain.model.user.Role.builder()
                .name("MODERATOR")
                .build();

        assertEquals("MODERATOR", role.getName());
    }

    @DisplayName("TEST 4: Should identify admin role")
    @Test
    void testIsAdmin() {
        com.techmate.techmate.domain.model.user.Role adminRole = com.techmate.techmate.domain.model.user.Role.builder()
                .name("ADMIN")
                .build();

        assertTrue(adminRole.isAdmin());
    }

    @DisplayName("TEST 5: Should identify moderator role")
    @Test
    void testIsModerator() {
        com.techmate.techmate.domain.model.user.Role modRole = com.techmate.techmate.domain.model.user.Role.builder()
                .name("MODERATOR")
                .build();

        assertTrue(modRole.isModerator());
    }

    @DisplayName("TEST 6: Should identify user role")
    @Test
    void testIsUser() {
        com.techmate.techmate.domain.model.user.Role userRole = com.techmate.techmate.domain.model.user.Role.builder()
                .name("USER")
                .build();

        assertTrue(userRole.isUser());
    }

    @DisplayName("TEST 7: Should handle case-insensitive role names")
    @Test
    void testCaseInsensitiveRoles() {
        com.techmate.techmate.domain.model.user.Role role = com.techmate.techmate.domain.model.user.Role.builder()
                .name("admin")
                .build();

        assertTrue(role.isAdmin());
    }

    @DisplayName("TEST 8: Should distinguish between different roles")
    @Test
    void testRoleDistinction() {
        com.techmate.techmate.domain.model.user.Role adminRole = com.techmate.techmate.domain.model.user.Role.builder()
                .name("ADMIN")
                .build();

        com.techmate.techmate.domain.model.user.Role userRole = com.techmate.techmate.domain.model.user.Role.builder()
                .name("USER")
                .build();

        assertTrue(adminRole.isAdmin());
        assertFalse(adminRole.isUser());
        assertTrue(userRole.isUser());
        assertFalse(userRole.isAdmin());
    }
}
