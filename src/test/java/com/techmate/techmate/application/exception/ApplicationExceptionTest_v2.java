package com.techmate.techmate.application.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Custom Exception Tests")
class ApplicationExceptionTest_v2 {

    @DisplayName("TEST 1: ResourceNotFoundException should exist")
    @Test
    void testResourceNotFoundExceptionExists() {
        assertNotNull(ResourceNotFoundException.class);
    }

    @DisplayName("TEST 2: BadRequestException should exist")
    @Test
    void testBadRequestExceptionExists() {
        assertNotNull(BadRequestException.class);
    }

    @DisplayName("TEST 3: ResourceNotFoundException with message")
    @Test
    void testResourceNotFoundExceptionWithMessage() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Not found");
        assertEquals("Not found", ex.getMessage());
    }

    @DisplayName("TEST 4: BadRequestException with message")
    @Test
    void testBadRequestExceptionWithMessage() {
        BadRequestException ex = new BadRequestException("Bad request");
        assertEquals("Bad request", ex.getMessage());
    }

    @DisplayName("TEST 5: ResourceNotFoundException with cause")
    @Test
    void testResourceNotFoundExceptionWithCause() {
        Throwable cause = new RuntimeException("Cause");
        ResourceNotFoundException ex = new ResourceNotFoundException("Not found", cause);
        assertEquals(cause, ex.getCause());
    }

    @DisplayName("TEST 6: Exceptions are throwable")
    @Test
    void testExceptionsAreThrowable() {
        assertTrue(Throwable.class.isAssignableFrom(ResourceNotFoundException.class));
        assertTrue(Throwable.class.isAssignableFrom(BadRequestException.class));
    }

    @DisplayName("TEST 7: Can instantiate exceptions")
    @Test
    void testCanInstantiateExceptions() {
        assertDoesNotThrow(() -> {
            throw new ResourceNotFoundException("test");
        });
    }

    @DisplayName("TEST 8: Exception naming follows convention")
    @Test
    void testExceptionNaming() {
        assertTrue(ResourceNotFoundException.class.getSimpleName().endsWith("Exception"));
        assertTrue(BadRequestException.class.getSimpleName().endsWith("Exception"));
    }
}
