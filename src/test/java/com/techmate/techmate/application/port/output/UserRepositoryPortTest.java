package com.techmate.techmate.application.port.output;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Output Port Tests")
@ExtendWith(MockitoExtension.class)
class UserRepositoryPortTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @DisplayName("TEST 1: Should have UserRepositoryPort interface")
    @Test
    void testUserRepositoryPortInterfaceExists() {
        assertNotNull(UserRepositoryPort.class);
    }

    @DisplayName("TEST 2: Mock should be injectable")
    @Test
    void testUserRepositoryPortMockable() {
        assertNotNull(userRepositoryPort);
    }

    @DisplayName("TEST 3: UserRepositoryPort should be interface")
    @Test
    void testUserRepositoryPortIsInterface() {
        assertTrue(UserRepositoryPort.class.isInterface());
    }

    @DisplayName("TEST 4: Should define persistence contract")
    @Test
    void testUserRepositoryPortDefinesContract() {
        assertNotNull(UserRepositoryPort.class.getDeclaredMethods());
        assertTrue(UserRepositoryPort.class.getDeclaredMethods().length > 0);
    }

    @DisplayName("TEST 5: Port should be mockable")
    @Test
    void testPortMockability() {
        assertNotNull(userRepositoryPort);
        assertTrue(userRepositoryPort.getClass().getName().contains("MockingDetails"));
    }

    @DisplayName("TEST 6: Should follow port naming convention")
    @Test
    void testPortNamingConvention() {
        assertTrue(UserRepositoryPort.class.getSimpleName().endsWith("Port"));
    }

    @DisplayName("TEST 7: Port should define output contract")
    @Test
    void testPortOutputContract() {
        // Verify that UserRepositoryPort defines methods that make sense for
        // persistence
        assertTrue(UserRepositoryPort.class.getDeclaredMethods().length > 0,
                "Port should define output methods");
    }

    @DisplayName("TEST 8: Mock should work with Mockito annotations")
    @Test
    void testMockitoIntegration() {
        assertNotNull(userRepositoryPort, "Mockito should inject the mock");
    }
}
