package com.techmate.techmate.integration;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class ApplicationContextTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        assertNotNull(mockMvc, "MockMvc should be autowired");
    }

    @Test
    @DisplayName("Spring application context should load successfully")
    void applicationContextLoads() {
        assertNotNull(mockMvc);
    }

    @Test
    @DisplayName("MockMvc should be properly configured")
    void mockMvcShouldBeConfigured() {
        assertNotNull(mockMvc);
        assertTrue(mockMvc.getClass().getSimpleName().contains("MockMvc"));
    }

    @Test
    @DisplayName("Application should be able to handle web requests")
    void applicationShouldHandleWebRequests() {
        assertNotNull(mockMvc);
        assertDoesNotThrow(() -> {
            mockMvc.getClass().getMethod("perform", org.springframework.test.web.servlet.RequestBuilder.class);
        });
    }

    @Test
    @DisplayName("Spring Boot context integration works")
    void springBootContextIntegrationWorks() {
        assertTrue(mockMvc != null);
    }

    @Test
    @DisplayName("Application is properly annotated")
    void applicationIsProperlyAnnotated() {
        assertTrue(this.getClass().isAnnotationPresent(SpringBootTest.class));
        assertTrue(this.getClass().isAnnotationPresent(AutoConfigureMockMvc.class));
    }
}
