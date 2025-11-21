package com.techmate.techmate.testutils;

/**
 * Helper inyectable para tests que expone conveniencias para crear JWTs.
 */
public class TestAuthUtils {

    public String createAdminToken(Integer id) {
        return JWTTestHelper.createTokenForAdmin(id);
    }

    public String createUserToken(Integer id) {
        return JWTTestHelper.createTokenForUser(id);
    }

    public String createTokenWithRoles(Integer id, String email, String username, String... roles) {
        return JWTTestHelper.createTokenWithRoles(id, email, username, roles);
    }
}

