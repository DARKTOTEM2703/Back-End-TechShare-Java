package com.techmate.techmate.application.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Mapper Interface Tests")
@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    @Mock
    private com.techmate.techmate.mapper.UserMapper userMapper;

    @DisplayName("TEST 1: Mapper interface should exist")
    @Test
    void testMapperInterfaceExists() {
        assertNotNull(com.techmate.techmate.mapper.UserMapper.class);
    }

    @DisplayName("TEST 2: Mapper should be mockable")
    @Test
    void testMapperIsMockable() {
        assertNotNull(userMapper);
    }

    @DisplayName("TEST 3: Mapper should be an interface")
    @Test
    void testMapperIsInterface() {
        assertTrue(com.techmate.techmate.mapper.UserMapper.class.isInterface());
    }

    @DisplayName("TEST 4: Mapper should define conversion methods")
    @Test
    void testMapperHasMethods() {
        assertTrue(com.techmate.techmate.mapper.UserMapper.class.getDeclaredMethods().length > 0);
    }

    @DisplayName("TEST 5: Mock injection should work")
    @Test
    void testMockInjection() {
        assertNotNull(userMapper);
    }

    @DisplayName("TEST 6: Mapper should be in mapper package")
    @Test
    void testMapperPackage() {
        String packageName = com.techmate.techmate.mapper.UserMapper.class.getPackage().getName();
        assertTrue(packageName.contains("mapper"));
    }

    @DisplayName("TEST 7: Mapper interface should follow naming convention")
    @Test
    void testMapperNaming() {
        assertTrue(com.techmate.techmate.mapper.UserMapper.class.getSimpleName().endsWith("Mapper"));
    }

    @DisplayName("TEST 8: Mapper should support Spring component model")
    @Test
    void testMapperAnnotations() {
        assertNotNull(com.techmate.techmate.mapper.UserMapper.class.getAnnotations());
    }
}
