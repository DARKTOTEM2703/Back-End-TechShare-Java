package com.techmate.techmate.testutils;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Small test utility that provides:
 * - an annotation @WithMockJwt(...) to mark tests (method or class) that should have a JWT available
 * - a JUnit5 extension that creates a token before each test and clears it afterwards
 * - a RequestPostProcessor jwt() to attach the token as Authorization header to MockMvc requests
 *
 * Usage example:
 *
 * @WithMockJwt(roles = {"ADMIN"}, userId = 7)
 * public void myTest() throws Exception {
 *     mockMvc.perform(get("/admin/whatever").with(MockMvcJwt.jwt()))...
 * }
 */
public class MockMvcJwt {

    private static final ThreadLocal<String> CURRENT_TOKEN = new ThreadLocal<>();

    // RequestPostProcessor to use in MockMvc.perform(...).with(MockMvcJwt.jwt())
    public static RequestPostProcessor jwt() {
        return request -> {
            String token = CURRENT_TOKEN.get();
            if (token == null) {
                // fallback: create a simple token without id using JWTTestHelper convenience
                token = JWTTestHelper.createTokenWithRoles("USER");
                CURRENT_TOKEN.set(token);
            }
            request.addHeader("Authorization", "Bearer " + token);
            return request;
        };
    }

    public static void setCurrentToken(String token) {
        CURRENT_TOKEN.set(token);
    }

    static void clearCurrentToken() {
        CURRENT_TOKEN.remove();
    }

    // Exposed for tests that need to stub service behavior based on the exact token string
    public static String getCurrentToken() {
        return CURRENT_TOKEN.get();
    }

    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @ExtendWith(WithMockJwtExtension.class)
    public @interface WithMockJwt {
        String[] roles() default {"USER"};
        int userId() default 1;
        String email() default "test@example.com";
        String username() default "tester";
    }

    public static class WithMockJwtExtension implements BeforeEachCallback, AfterEachCallback {

        @Override
        public void beforeEach(ExtensionContext context) throws Exception {
            Optional<WithMockJwt> ann = findAnnotation(context);
            if (ann.isPresent()) {
                WithMockJwt a = ann.get();
                // create token with id, email, username and roles
                String token = JWTTestHelper.createTokenWithRoles(a.userId(), a.email(), a.username(), a.roles());
                setCurrentToken(token);
            }
        }

        @Override
        public void afterEach(ExtensionContext context) throws Exception {
            clearCurrentToken();
        }

        private Optional<WithMockJwt> findAnnotation(ExtensionContext context) {
            Method m = context.getTestMethod().orElse(null);
            if (m != null && m.isAnnotationPresent(WithMockJwt.class)) {
                return Optional.of(m.getAnnotation(WithMockJwt.class));
            }
            Class<?> cls = context.getTestClass().orElse(null);
            if (cls != null && cls.isAnnotationPresent(WithMockJwt.class)) {
                return Optional.of(cls.getAnnotation(WithMockJwt.class));
            }
            return Optional.empty();
        }
    }
}

