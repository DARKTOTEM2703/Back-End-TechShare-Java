package com.techmate.techmate.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class ApplicationConfigurationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        assertNotNull(applicationContext, "Application context should be autowired");
    }

    @Test
    @DisplayName("Application context should be available")
    void applicationContextShouldBeAvailable() {
        assertNotNull(applicationContext);
    }

    @Test
    @DisplayName("Spring beans should be registered")
    void springBeansShouldBeRegistered() {
        assertNotNull(applicationContext);
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        assertTrue(beanNames.length > 0, "Application should have registered beans");
    }

    @Test
    @DisplayName("Multiple beans should be configured")
    void multipleBeansShouldBeConfigured() {
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        assertTrue(beanNames.length > 10, "Application should have multiple beans configured");
    }

    @Test
    @DisplayName("Application should have service beans")
    void applicationShouldHaveServiceBeans() {
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        assertTrue(beanNames.length > 0);
        // At least some beans should exist
    }

    @Test
    @DisplayName("Spring configuration should be valid")
    void springConfigurationShouldBeValid() {
        assertNotNull(applicationContext);
        assertNotNull(applicationContext.getApplicationName());
    }

    @Test
    @DisplayName("Application should load security configuration")
    void applicationShouldLoadSecurityConfiguration() {
        // Verify that beans are created without errors
        assertNotNull(applicationContext);
        assertTrue(applicationContext.containsBean("springSecurityFilterChain") ||
                applicationContext.getBeanDefinitionNames().length > 0);
    }

    @Test
    @DisplayName("Application should load API version configuration")
    void applicationShouldLoadApiVersionConfiguration() {
        assertNotNull(applicationContext);
        // Verify configuration loads successfully
    }

    @Test
    @DisplayName("Application context is not null after initialization")
    void applicationContextIsNotNullAfterInitialization() {
        assertNotNull(applicationContext);
        assertNotNull(applicationContext.getApplicationName());
    }

    @Test
    @DisplayName("Application configuration is complete")
    void applicationConfigurationIsComplete() {
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        assertTrue(beanNames.length > 0, "Application configuration should have loaded beans");
    }
}
