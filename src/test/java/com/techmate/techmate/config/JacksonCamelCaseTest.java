package com.techmate.techmate.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techmate.techmate.entity.Borrow;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

/**
 * Unit test verifying that Jackson serializes domain models
 * using CAMEL_CASE naming convention (default Java/JavaScript standard).
 * 
 * This test ensures compatibility with Next.js frontend which expects
 * camelCase JSON properties.
 */
public class JacksonCamelCaseTest {

    @Test
    void jacksonShouldSerializeBorrowWithCamelCase() throws Exception {
        // Using default ObjectMapper configuration (no explicit naming strategy)
        // which defaults to CAMEL_CASE
        ObjectMapper mapper = new ObjectMapper();

        Borrow b = new Borrow();
        b.setId(123);
        b.setDate(new Date());
        b.setAmount(BigDecimal.valueOf(10.5));
        b.setStatus(com.techmate.techmate.entity.Status.PENDING);

        String json = mapper.writeValueAsString(b);
        
        // Verify camelCase serialization (NOT snake_case)
        assertTrue(json.contains("\"id\":123"), 
            "Expected 'id' in camelCase, got: " + json);
        assertTrue(json.contains("\"amount\":") || json.contains("\"amount\":10.5"),
            "Expected 'amount' field in camelCase, got: " + json);
        assertTrue(json.contains("\"status\":\"PENDING\""),
            "Expected 'status' field in camelCase, got: " + json);
        
        // Verify NO snake_case is present (the old format)
        assertFalse(json.contains("borrow_id"),
            "Should NOT contain snake_case 'borrow_id', got: " + json);
        assertFalse(json.contains("issue_date"),
            "Should NOT contain snake_case 'issue_date', got: " + json);
    }

    @Test
    void jacksonConfigObjectMapperShouldUseCamelCase() throws Exception {
        // Test the static objectMapper() utility from JacksonConfig
        ObjectMapper mapper = JacksonConfig.objectMapper();

        Borrow b = new Borrow();
        b.setId(456);
        b.setAmount(BigDecimal.valueOf(99.99));
        b.setStatus(com.techmate.techmate.entity.Status.BORROWED);

        String json = mapper.writeValueAsString(b);
        
        // Verify camelCase in serialized JSON
        assertTrue(json.contains("\"id\":456"),
            "Expected camelCase 'id' field, got: " + json);
        assertTrue(json.contains("\"amount\":") || json.contains("\"amount\":99.99"),
            "Expected camelCase 'amount' field, got: " + json);
        
        // Ensure no snake_case format
        assertFalse(json.toLowerCase().contains("_id") && json.contains("borrow"),
            "Should not contain snake_case 'borrow_id', got: " + json);
    }
}
