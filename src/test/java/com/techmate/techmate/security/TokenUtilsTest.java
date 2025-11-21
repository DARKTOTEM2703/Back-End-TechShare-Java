package com.techmate.techmate.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class TokenUtilsTest {

    @AfterEach
    public void cleanup() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void createAndDecodeToken_returnsClaims() {
        Integer id = 123;
        String email = "user@example.com";
        String username = "user";
        List<String> roles = List.of("ADMIN");
        List<Integer> idRoles = List.of(1);

        String token = TokenUtils.createToken(id, email, username, roles, idRoles);
        assertThat(token).isNotNull();

        Integer parsedId = TokenUtils.getUserIdFromToken(token);
        String parsedUser = TokenUtils.getUserNameFromToken(token);
        assertThat(parsedId).isEqualTo(id);
        assertThat(parsedUser).isEqualTo(username);

        var opt = TokenUtils.getRolesFromToken(token);
        assertThat(opt).isPresent();
        assertThat(opt.get()).containsExactlyElementsOf(idRoles);
    }

    @Test
    public void getAuthentication_validToken_returnsAuthentication() {
        Integer id = 10;
        String email = "auth@example.com";
        String username = "authuser";
        List<String> roles = List.of("ADMIN");

        String token = TokenUtils.createToken(id, email, username, roles, List.of(2));
        var auth = TokenUtils.getAuthentication(token);

        assertThat(auth).isNotNull();
        assertThat(auth.getName()).isEqualTo(email);
        assertThat(auth.getAuthorities()).isNotEmpty();
    }

    @Test
    public void invalidToken_returnsNull() {
        assertNull(TokenUtils.getAuthentication("invalid.token.here"));
        assertNull(TokenUtils.decodeToken("not-a-jwt"));
    }

    @Test
    public void getAuthenticatedUserRole_readsFromSecurityContext() {
        var auth = new UsernamePasswordAuthenticationToken("u@x.com", null,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        String role = TokenUtils.getAuthenticatedUserRole();
        assertThat(role).isEqualTo("ROLE_USER");
    }
}

