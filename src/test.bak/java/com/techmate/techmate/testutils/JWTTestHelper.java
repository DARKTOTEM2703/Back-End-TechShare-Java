package com.techmate.techmate.testutils;

import com.techmate.techmate.security.TokenUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Helper para crear tokens JWT en tests.
 * Envuelve a TokenUtils.createToken(...) añadiendo conveniencia para roles.
 */
public final class JWTTestHelper {

    private JWTTestHelper() {
        // utilitario
    }

    /**
     * Crea un token con los roles indicados. Si un role no empieza por "ROLE_" se le prefija.
     */
    public static String createTokenWithRoles(Integer id, String email, String username, String... roles) {
        List<String> rolesList = Arrays.stream(Objects.requireNonNull(roles))
                .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                .collect(Collectors.toList());

        return TokenUtils.createToken(id, email, username, rolesList, null);
    }

    public static String createTokenWithRoles(String... roles) {
        return createTokenWithRoles(1, "test@example.com", "test", roles);
    }

    public static String createTokenForAdmin(Integer id) {
        return createTokenWithRoles(id, "admin@example.com", "admin", "ADMIN");
    }

    public static String createTokenForUser(Integer id) {
        return createTokenWithRoles(id, "user@example.com", "user", "USER");
    }
}

