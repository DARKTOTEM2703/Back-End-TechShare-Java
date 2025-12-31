package com.techmate.techmate.domain.model.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@DisplayName("User Domain Model Tests")
class UserTest {

    @DisplayName("TEST 1: Should create User using Builder pattern")
    @Test
    void testUserCreationWithBuilder() {
        User user = new User.Builder()
                .id(1)
                .username("testuser")
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .passwordHash("hash123")
                .enabled(true)
                .build();

        assertNotNull(user);
        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
    }

    @DisplayName("TEST 2: Should return correct username")
    @Test
    void testGetUsername() {
        User user = new User.Builder()
                .username("john_doe")
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .passwordHash("hash123")
                .build();

        assertEquals("john_doe", user.getUsername());
    }

    @DisplayName("TEST 3: Should return correct email")
    @Test
    void testGetEmail() {
        User user = new User.Builder()
                .email("john@example.com")
                .username("john")
                .firstName("John")
                .lastName("Doe")
                .passwordHash("hash123")
                .build();

        assertEquals("john@example.com", user.getEmail());
    }

    @DisplayName("TEST 4: Should return correct id")
    @Test
    void testGetId() {
        User user = new User.Builder()
                .id(42)
                .username("user42")
                .firstName("Test")
                .lastName("User")
                .email("user42@example.com")
                .passwordHash("hash123")
                .build();

        assertEquals(42, user.getId());
    }

    @DisplayName("TEST 5: Should track enabled status")
    @Test
    void testUserEnabledStatus() {
        User enabledUser = new User.Builder()
                .enabled(true)
                .username("active")
                .firstName("Active")
                .lastName("User")
                .email("active@example.com")
                .passwordHash("hash123")
                .build();

        assertTrue(enabledUser.isEnabled());

        User disabledUser = new User.Builder()
                .enabled(false)
                .username("inactive")
                .firstName("Inactive")
                .lastName("User")
                .email("inactive@example.com")
                .passwordHash("hash123")
                .build();

        assertFalse(disabledUser.isEnabled());
    }

    @DisplayName("TEST 6: Should store first and last name")
    @Test
    void testUserNames() {
        User user = new User.Builder()
                .firstName("John")
                .lastName("Doe")
                .username("johndoe")
                .email("john@example.com")
                .passwordHash("hash123")
                .build();

        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
    }

    @DisplayName("TEST 7: Should handle role names collection")
    @Test
    void testUserRoles() {
        Set<String> roles = new HashSet<>();
        roles.add("ADMIN");
        roles.add("USER");

        User user = new User.Builder()
                .roleNames(roles)
                .username("admin")
                .firstName("Admin")
                .lastName("User")
                .email("admin@example.com")
                .passwordHash("hash123")
                .build();

        assertNotNull(user.getRoleNames());
        assertTrue(user.getRoleNames().contains("ADMIN"));
    }

    @DisplayName("TEST 8: Should store timestamps")
    @Test
    void testUserTimestamps() {
        LocalDateTime now = LocalDateTime.now();

        User user = new User.Builder()
                .createdAt(now)
                .updatedAt(now)
                .username("test")
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .passwordHash("hash123")
                .build();

        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
    }
}
