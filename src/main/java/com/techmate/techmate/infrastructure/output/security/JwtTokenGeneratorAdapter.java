package com.techmate.techmate.infrastructure.output.security;

import org.springframework.stereotype.Component;
import com.techmate.techmate.core.application.port.output.TokenGeneratorPort;
import com.techmate.techmate.infrastructure.security.TokenUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtTokenGeneratorAdapter implements TokenGeneratorPort {

    // TokenUtils usa métodos estáticos, no necesita inyección
    public JwtTokenGeneratorAdapter() {
    }

    @Override
    public String generateToken(Integer userId, String username, String email, List<String> roles) {
        // Convertir roles de String a Integer si es necesario
        List<Integer> roleIds = (roles != null && !roles.isEmpty()) ? roles.stream()
                .map(role -> {
                    return switch (role.toUpperCase()) {
                        case "ADMIN" -> 2;
                        case "MODERATOR" -> 3;
                        default -> 1; // Default to USER
                    };
                })
                .toList() : new ArrayList<>();

        return TokenUtils.createToken(userId, email, username, roles, roleIds);
    }

    @Override
    public Integer extractUserId(String token) {
        return TokenUtils.getUserIdFromToken(token);
    }

    @Override
    public String extractUsername(String token) {
        return TokenUtils.getUsernameFromToken(token);
    }

    @Override
    public List<String> extractRoles(String token) {
        var rolesOptional = TokenUtils.getRolesFromToken(token);
        // Convertir Integer roles de vuelta a String
        return rolesOptional.map(intRoles -> intRoles.stream()
                .map(roleId -> switch (roleId) {
                    case 2 -> "ADMIN";
                    case 3 -> "MODERATOR";
                    default -> "USER";
                })
                .toList()).orElse(new ArrayList<>());
    }

    @Override
    public boolean isValid(String token) {
        try {
            // Usar un UserDetails dummy para validar el token
            UserDetails dummyUser = User.builder()
                    .username(extractUsername(token))
                    .password("")
                    .authorities(new ArrayList<>())
                    .build();
            return TokenUtils.validateToken(token, dummyUser);
        } catch (Exception e) {
            return false;
        }
    }
}
