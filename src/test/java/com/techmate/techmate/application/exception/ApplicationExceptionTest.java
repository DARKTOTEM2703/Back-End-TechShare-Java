package com.techmate.techmate.application.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Custom Exception Tests")
class ApplicationExceptionTest {

    @DisplayName("TEST 1: ResourceNotFoundException should be throwable with message")
    @Test
    void testResourceNotFoundExceptionWithMessage() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Resource not found");

        assertNotNull(exception);
        assertEquals("Resource not found", exception.getMessage());
    }

    @DisplayName("TEST 2: ResourceNotFoundException should be throwable with cause")
    @Test
    void testResourceNotFoundExceptionWithCause() {
        Throwable cause = new RuntimeException("Original cause");
        ResourceNotFoundException exception = new ResourceNotFoundException("Resource not found", cause);

        assertNotNull(exception);
        assertEquals("Resource not found", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @DisplayName("TEST 3: DuplicateResourceException should be throwable")
    @Test
    void testDuplicateResourceException() {
        DuplicateResourceException exception = new DuplicateResourceException("Duplicate found");

        assertNotNull(exception);
        assertEquals("Duplicate found", exception.getMessage());
    }

    @DisplayName("TEST 4: InvalidCredentialsException should be throwable")
    @Test
    void testInvalidCredentialsException() {
        InvalidCredentialsException exception = new InvalidCredentialsException("Invalid credentials");

        assertNotNull(exception);
        assertEquals("Invalid credentials", exception.getMessage());
    }

    @DisplayName("TEST 5: UnauthorizedException should be throwable")
    @Test
    void testUnauthorizedException() {
        UnauthorizedException exception = new UnauthorizedException("Unauthorized");

        assertNotNull(exception);
        assertEquals("Unauthorized", exception.getMessage());
    }

    @DisplayName("TEST 6: BadRequestException should be throwable")
    @Test
    void testBadRequestException() {
        BadRequestException exception = new BadRequestException("Bad request");

        assertNotNull(exception);
        assertEquals("Bad request", exception.getMessage());
    }

    @DisplayName("TEST 7: Exceptions should be RuntimeException subclasses")
    @Test
    void testExceptionHierarchy() {
        assertTrue(RuntimeException.class.isAssignableFrom(ResourceNotFoundException.class));
        assertTrue(RuntimeException.class.isAssignableFrom(BadRequestException.class));
    }

    @DisplayName("TEST 8: Exceptions should have messages")
    @Test
    void testExceptionMessages() {
        String message = "Test error";
        ResourceNotFoundException ex = new ResourceNotFoundException(message);
        assertTrue(ex.getMessage().contains("Test error"));
    }

}
