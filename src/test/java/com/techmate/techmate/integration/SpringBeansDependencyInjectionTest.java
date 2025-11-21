package com.techmate.techmate.integration;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Spring Beans and Dependency Injection Tests")
class SpringBeansDependencyInjectionTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    @DisplayName("Application context should be loaded")
    void applicationContextShouldBeLoaded() {
        assertNotNull(applicationContext);
    }

    @Test
    @DisplayName("Application context should have an ID")
    void applicationContextShouldHaveAnId() {
        String id = applicationContext.getId();
        assertNotNull(id);
    }

    @Test
    @DisplayName("Application context should have a display name")
    void applicationContextShouldHaveDisplayName() {
        String displayName = applicationContext.getDisplayName();
        assertNotNull(displayName);
        assertFalse(displayName.isEmpty());
    }

    @Test
    @DisplayName("Application context should contain beans")
    void applicationContextShouldContainBeans() {
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        assertNotNull(beanNames);
        assertTrue(beanNames.length > 0);
    }

    @Test
    @DisplayName("Application context should contain bean definitions")
    void applicationContextShouldContainBeanDefinitions() {
        int beanCount = applicationContext.getBeanDefinitionCount();
        assertTrue(beanCount > 0);
    }

    @Test
    @DisplayName("Application context should be able to get bean by name")
    void applicationContextShouldBeAbleToGetBeanByName() {
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        if (beanNames.length > 0) {
            String firstBean = beanNames[0];
            Object bean = applicationContext.getBean(firstBean);
            assertNotNull(bean);
        }
    }

    @Test
    @DisplayName("Application context should have parent context if available")
    void applicationContextShouldHandleParentContext() {
        ApplicationContext parent = applicationContext.getParent();
        // Parent context is optional, so we just check that we can get it
        assertTrue(parent == null || parent instanceof ApplicationContext);
    }

    @Test
    @DisplayName("Application context should support multiple bean queries")
    void applicationContextShouldSupportMultipleBeanQueries() {
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            assertNotNull(beanName);
            assertTrue(applicationContext.containsBeanDefinition(beanName));
        }
    }

    @Test
    @DisplayName("Application context should provide bean name list")
    void applicationContextShouldProvideBeanNameList() {
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        assertTrue(beanNames != null && beanNames.length > 0);
    }

    @Test
    @DisplayName("Application context initialization should complete successfully")
    void applicationContextInitializationShouldCompleteSuccessfully() {
        assertNotNull(applicationContext);
        assertTrue(applicationContext.getBeanDefinitionCount() > 0);
    }
}

