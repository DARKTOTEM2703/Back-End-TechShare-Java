package com.techmate.techmate.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@DisplayName("HTTP Response Handling Tests")
class HttpResponseHandlingTest {

    @Test
    @DisplayName("ResponseEntity should handle OK status")
    void responseEntityShouldHandleOkStatus() {
        ResponseEntity<String> response = ResponseEntity.ok("Success");
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Success", response.getBody());
    }

    @Test
    @DisplayName("ResponseEntity should handle status codes")
    void responseEntityShouldHandleStatusCodes() {
        ResponseEntity<String> created = ResponseEntity.status(HttpStatus.CREATED).body("Created");
        ResponseEntity<String> badRequest = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error");

        assertEquals(HttpStatus.CREATED, created.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, badRequest.getStatusCode());
    }

    @Test
    @DisplayName("ResponseEntity should handle NOT_FOUND")
    void responseEntityShouldHandleNotFound() {
        ResponseEntity<Void> notFound = ResponseEntity.notFound().build();
        
        assertEquals(HttpStatus.NOT_FOUND, notFound.getStatusCode());
    }

    @Test
    @DisplayName("ResponseEntity should handle empty body")
    void responseEntityShouldHandleEmptyBody() {
        ResponseEntity<String> response = ResponseEntity.ok("");
        
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    @DisplayName("ResponseEntity should handle NULL body")
    void responseEntityShouldHandleNullBody() {
        ResponseEntity<String> response = ResponseEntity.ok(null);
        
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("ResponseEntity status should be retrievable")
    void responseEntityStatusShouldBeRetrievable() {
        ResponseEntity<String> response = ResponseEntity.ok("test");
        
        assertNotNull(response.getStatusCode());
        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @Test
    @DisplayName("ResponseEntity should handle generic types")
    void responseEntityShouldHandleGenericTypes() {
        ResponseEntity<Integer> intResponse = ResponseEntity.ok(42);
        ResponseEntity<Boolean> boolResponse = ResponseEntity.ok(true);

        assertEquals(42, intResponse.getBody());
        assertTrue(boolResponse.getBody());
    }

    @Test
    @DisplayName("ResponseEntity should handle error responses")
    void responseEntityShouldHandleErrorResponses() {
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Server Error");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getStatusCode().is5xxServerError());
    }

    @Test
    @DisplayName("ResponseEntity should handle 4xx errors")
    void responseEntityShouldHandle4xxErrors() {
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Unauthorized");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    @DisplayName("ResponseEntity.ok should be idempotent")
    void responseEntityOkShouldBeIdempotent() {
        ResponseEntity<String> response1 = ResponseEntity.ok("test");
        ResponseEntity<String> response2 = ResponseEntity.ok("test");

        assertEquals(response1.getStatusCode(), response2.getStatusCode());
    }
}

