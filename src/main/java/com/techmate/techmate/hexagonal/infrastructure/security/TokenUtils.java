package com.techmate.techmate.hexagonal.infrastructure.security;

import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

import com.techmate.techmate.hexagonal.infrastructure.dto.AuthUserDTO;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class TokenUtils {

    // Secret key is initialized at application startup via JwtConfig
    private static volatile String ACCESS_TOKEN_SECRET;

    // Validez en segundos (por defecto 3600 = 1 hora). Se puede configurar con JWT_EXPIRATION_SECONDS
    private static final Long ACCESS_TOKEN_VALIDITY_SECONDS;
    static {
        Long defaultSeconds = 3600L;
        String s = System.getenv("JWT_EXPIRATION_SECONDS");
        if (s != null && !s.isBlank()) {
            try { defaultSeconds = Long.parseLong(s); } catch (NumberFormatException ignore) {}
        }
        ACCESS_TOKEN_VALIDITY_SECONDS = defaultSeconds;
    }

    // Método para crear el token
    // Modifica el método para aceptar el id del usuario
    public static String createToken(Integer id, String email, String userName, List<String> roles, List<Integer> idRoles) {
        Long expirationTime = ACCESS_TOKEN_VALIDITY_SECONDS * 1_000;
        Date expirationDate = new Date(System.currentTimeMillis() + expirationTime);
    
        // Añadir el id, nombre de usuario, roles, y idRoles a los claims personalizados
        Map<String, Object> extra = new HashMap<>();
        extra.put("id", id);   // Aquí se añade el id del usuario
        extra.put("user_name", userName); // Aquí se añade el nombre de usuario
        extra.put("roles", roles); // Aquí se añaden los roles del usuario
        extra.put("idRoles", idRoles);
    
    SecretKey secretKey = getSecretKey();

    return Jwts.builder()
                .setSubject(email)
                .setExpiration(expirationDate)
                .addClaims(extra)  // Añadir los claims extra (id, nombre de usuario, roles, idRoles)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }
    

    // Método para obtener la autenticación a partir del token
    public static UsernamePasswordAuthenticationToken getAuthentication(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String email = claims.getSubject();
            
            // Obtener roles del token de forma type-safe
            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) claims.get("roles");
            if (roles == null) roles = List.of();
            
            // Obtener datos adicionales del token
            Integer userId = ((Number) claims.get("id")).intValue();
            String userName = (String) claims.get("user_name");
            
            // Crear UserDetailsImpl con datos primitivos para evitar ConcurrentModificationException
            // NO crear entidades Usuario que pueden activar persistence context de Hibernate
            AuthUserDTO authUser = new AuthUserDTO(userId, userName, email, null, 
                                                  null, null, true, roles);
            UserDetailsImpl userDetails = new UserDetailsImpl(authUser);
            
            // Convertir roles a authorities
            var authorities = roles.stream()
                                   .map(role -> new SimpleGrantedAuthority(role))
                                   .collect(Collectors.toList());

            return new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
        } catch (JwtException e) {
            return null; // Retorna null si hay un error con el token
        }
    }

    // Método para obtener el rol del usuario autenticado
    public static String getAuthenticatedUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null) {
            // Si el principal es UserDetails, obtener authorities desde ahí
            if (authentication.getPrincipal() instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                return userDetails.getAuthorities().stream()
                                  .findFirst() // Ajusta si el usuario puede tener varios roles
                                  .map(GrantedAuthority::getAuthority)
                                  .orElse(null); // Devuelve el rol o null si no tiene ninguno
            }
            
            // Si no es UserDetails, obtener authorities directamente del Authentication
            // (útil para tests o autenticaciones personalizadas)
            return authentication.getAuthorities().stream()
                                .findFirst()
                                .map(GrantedAuthority::getAuthority)
                                .orElse(null);
        }
        return null;
    }
    

    public static Integer getUserIdFromToken(String token) {
        Claims claims = decodeToken(token); // Decodificar el token
        if (claims != null) {
            // Recuperar el id de los claims
            Object idClaim = claims.get("id");
            if (idClaim != null) {
                try {
                    // Manejar Integer, Long, String o Number
                    if (idClaim instanceof Integer) {
                        return (Integer) idClaim;
                    } else if (idClaim instanceof Long) {
                        return ((Long) idClaim).intValue();
                    } else if (idClaim instanceof Number) {
                        return ((Number) idClaim).intValue();
                    } else {
                        return Integer.parseInt(idClaim.toString());
                    }
                } catch (NumberFormatException | ClassCastException e) {
                    throw new RuntimeException("ID en token no es un número válido: " + idClaim, e);
                }
            }
        }
        throw new RuntimeException("Token no válido o ID no encontrado");
    }
    
    public static String getUserNameFromToken(String token) {
        Claims claims = decodeToken(token); // Decodificar el token
        if (claims != null) {
            Object userNameClaim = claims.get("user_name");
            if (userNameClaim != null) return userNameClaim.toString();
        }
        throw new RuntimeException("Token no válido o nombre de usuario no encontrado");
    }
    
    /**
     * Extrae el email (username) del token JWT
     */
    public static String getUsernameFromToken(String token) {
        Claims claims = decodeToken(token);
        if (claims != null) {
            return claims.getSubject(); // El email se guarda en el subject
        }
        throw new RuntimeException("Token no válido o email no encontrado");
    }
    
    /**
     * Valida si el token es válido y no ha expirado
     */
    public static boolean validateToken(String token, UserDetails userDetails) {
        try {
            Claims claims = decodeToken(token);
            if (claims == null) return false;
            
            String emailFromToken = claims.getSubject();
            Date expirationDate = claims.getExpiration();
            
            // Verificar que el email coincida y el token no haya expirado
            return emailFromToken.equals(userDetails.getUsername()) && 
                   !expirationDate.before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
    
    

    public static Optional<List<Integer>> getRolesFromToken(String token) {
        Claims claims = decodeToken(token); // Decodificar el token
        if (claims != null) {
            Object rolesClaim = claims.get("idRoles");
            if (rolesClaim != null) {
                try {
                    @SuppressWarnings("unchecked")
                    List<Integer> roles = (List<Integer>) rolesClaim; // Cast a List<Integer>
                    return Optional.of(roles);
                } catch (ClassCastException e) {
                    // ignore and return empty
                }
            }
        }
        return Optional.empty(); // Devuelve un Optional vacío si no se encuentra
    }
    
    

    public static Claims decodeToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims;
        } catch (JwtException e) {
            return null;
        }
    }
    
    private static SecretKey getSecretKey() {
        // If not initialized via JwtConfig, attempt common fallbacks useful for tests:
        if (ACCESS_TOKEN_SECRET == null || ACCESS_TOKEN_SECRET.isBlank()) {
            // 1) System property set via -DJWT_SECRET=...
            String fromSysProp = System.getProperty("JWT_SECRET");
            if (fromSysProp != null && !fromSysProp.isBlank()) {
                ACCESS_TOKEN_SECRET = fromSysProp;
            }
        }

        if (ACCESS_TOKEN_SECRET == null || ACCESS_TOKEN_SECRET.isBlank()) {
            // 2) Environment variable (useful in CI or manual runs)
            String fromEnv = System.getenv("JWT_SECRET");
            if (fromEnv != null && !fromEnv.isBlank()) {
                ACCESS_TOKEN_SECRET = fromEnv;
            }
        }

        if (ACCESS_TOKEN_SECRET == null || ACCESS_TOKEN_SECRET.isBlank()) {
            // 3) Try loading test properties from classpath (src/test/resources/application-test.properties)
            try (var is = TokenUtils.class.getClassLoader().getResourceAsStream("application-test.properties")) {
                if (is != null) {
                    var props = new java.util.Properties();
                    props.load(is);
                    String prop = props.getProperty("JWT_SECRET");
                    if (prop != null && !prop.isBlank()) {
                        ACCESS_TOKEN_SECRET = prop;
                    }
                }
            } catch (Exception ignore) {
                // ignore, we'll throw below if still missing
            }
        }

        if (ACCESS_TOKEN_SECRET == null || ACCESS_TOKEN_SECRET.isBlank()) {
            // As a last resort, allow generation of an ephemeral secret for test/dev contexts where
            // tests may call TokenUtils directly without starting the Spring context. We detect a
            // test/dev intention via common properties. If running in production-like env, fail fast.
            String activeProfile = System.getProperty("spring.profiles.active");
            if (activeProfile == null || activeProfile.isBlank()) {
                activeProfile = System.getenv("SPRING_PROFILES_ACTIVE");
            }
            boolean isTestOrDev = activeProfile != null && (activeProfile.equalsIgnoreCase("test") || activeProfile.equalsIgnoreCase("dev"));

            if (isTestOrDev) {
                byte[] key = new byte[32];
                new java.security.SecureRandom().nextBytes(key);
                ACCESS_TOKEN_SECRET = java.util.Base64.getEncoder().encodeToString(key);
                return Keys.hmacShaKeyFor(ACCESS_TOKEN_SECRET.getBytes());
            }

            throw new IllegalStateException("JWT secret is not configured. Set JWT_SECRET environment/property before using tokens.");
        }

        return Keys.hmacShaKeyFor(ACCESS_TOKEN_SECRET.getBytes());
    }

    /**
     * Initialize the static secret used by TokenUtils. This should be called once at application startup.
     */
    public static void init(String secret) {
        ACCESS_TOKEN_SECRET = secret;
    }
    
}




