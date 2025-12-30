package com.techmate.techmate.hexagonal.infrastructure.security;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.techmate.techmate.hexagonal.infrastructure.dto.ApiErrorResponse;
import org.springframework.http.HttpStatus;

/**
 * Filtro de autenticación JWT.
 *
 * Descripción general (flujo):
 * 1) `attemptAuthentication` lee JSON {email, password} del body y crea un
 * UsernamePasswordAuthenticationToken que delega al AuthenticationManager.
 * 2) Si la autenticación es correcta, `successfulAuthentication` se encarga de
 * generar el JWT (vía TokenUtils), añadirlo a la cabecera `Authorization`
 * y devolver también un JSON con datos mínimos (útil para SPAs).
 *
 * Buenas prácticas y motivos de diseño:
 * - Leemos JSON en lugar de parámetros form para evitar el prompt de BasicAuth
 * y para facilitar clientes SPA que envían JSON.
 * - Devolvemos el token tanto en cabecera como en el body JSON porque algunos
 * clientes prefieren leer directamente el JSON (easier testing) y otros usan
 * la cabecera; ambos enfoques están soportados.
 * - No se llama a `super.successfulAuthentication` para evitar que Spring
 * desencadene comportamientos de login basados en sesiones (la app es
 * stateless y usa JWT).
 * - IMPORTANTE: Siempre usar HTTPS en producción para evitar que el token sea
 * interceptado (TLS protege la cabecera y el body). Considerar refresh tokens
 * y revocación si necesitas logout/rotación de tokens.
 */
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final Logger log = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        AuthCredentials authCredentials;

        try {
            // Leemos todo el cuerpo como String (para poder limpiar BOM y whitespace)
            StringBuilder sb = new StringBuilder();
            String line;
            var reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String payload = sb.toString();
            if (payload == null)
                payload = "";
            // Quitar BOM u otros caracteres invisibles al inicio que rompen el parser
            payload = payload.replace("\uFEFF", "").trim();

            if (payload.isEmpty()) {
                log.warn("Attempted authentication with empty payload");
                throw new AuthenticationServiceException("La solicitud de autenticación está vacía");
            }

            authCredentials = com.techmate.techmate.hexagonal.infrastructure.config.JacksonConfig.objectMapper().readValue(payload,
                    AuthCredentials.class);
        } catch (IOException e) {
            log.warn("Attempted authentication with invalid payload: {}", e.getMessage());
            // Devolver un error que Spring Security podrá transformar en 401/400 según
            // configuración
            throw new AuthenticationServiceException("Solicitud de autenticación inválida");
        }

        UsernamePasswordAuthenticationToken usernamePAT = new UsernamePasswordAuthenticationToken(
                authCredentials.getEmail(),
                authCredentials.getPassword(),
                List.of());

        return getAuthenticationManager().authenticate(usernamePAT);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException failed) throws IOException, ServletException {
        log.warn("Autenticación fallida para la petición {}: {}", request.getRequestURI(), failed.getMessage());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        ApiErrorResponse body = ApiErrorResponse.builder()
                .timestamp(java.time.LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("No autorizado")
                .message(failed.getMessage() != null ? failed.getMessage() : "Autenticación fallida")
                .path(request.getRequestURI())
                .code("UNAUTHENTICATED")
                .validationErrors(java.util.List.of())
                .build();

        com.techmate.techmate.hexagonal.infrastructure.config.JacksonConfig.objectMapper().writeValue(response.getWriter(), body);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authResult) throws IOException, ServletException {

        // Obtenemos los detalles del usuario autenticado (implementación propia)
        UserDetailsImpl userDetails = (UserDetailsImpl) authResult.getPrincipal();
        Integer userId = userDetails.getId(); // FIXED: Usar método directo sin crear entidad Usuario

        // Seguridad adicional: si el usuario existe pero no está habilitado se
        // devuelve 401 con un mensaje claro. Esto evita que usuarios no
        // verificados obtengan tokens.
        if (!userDetails.isEnabled()) { // FIXED: Usar método directo sin crear entidad Usuario
            log.info("Usuario {} inició sesión pero no está verificado", userDetails.getUsername());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            Map<String, Object> payload = new HashMap<>();
            payload.put("error", "Cuenta no verificada");
            com.techmate.techmate.hexagonal.infrastructure.config.JacksonConfig.objectMapper().writeValue(response.getWriter(), payload);
            return;
        }

        // Extraemos roles e ids de roles para incluirlos en el token si se desea
        List<Integer> roleIdList = userDetails.getIdRoles();
        Collection<? extends GrantedAuthority> roles = userDetails.getRoles();
        List<String> roleList = roles.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        String userName = userDetails.getNombre();

        // Generación del token: la implementación concreta está en TokenUtils.
        // TokenUtils debe encargarse de:
        // - Firmar el token con secret seguro
        // - Añadir expiración (exp)
        // - Incluir los claims necesarios (sub, id, roles)
        String token = TokenUtils.createToken(userId, userDetails.getUsername(), userName, roleList, roleIdList);

        // Se añade la cabecera Authorization para compatibilidad con clientes que
        // consumen la cabecera. Además devolvemos JSON con el token y datos
        // mínimos para facilitar el consumo desde SPAs.
        // Nota: en producción, usar siempre HTTPS para proteger este token.
        response.addHeader("Authorization", "Bearer " + token);
        response.setContentType("application/json;charset=UTF-8");

        Map<String, Object> resp = new HashMap<>();
        resp.put("token", "Bearer " + token);
        resp.put("user_id", userId);
        resp.put("user_name", userName);
        resp.put("email", userDetails.getUsername());
        resp.put("roles", roleList);

        com.techmate.techmate.hexagonal.infrastructure.config.JacksonConfig.objectMapper().writeValue(response.getWriter(), resp);

        log.info("Usuario {} autenticado correctamente, id={}, roles={}", userDetails.getUsername(), userId, roleList);

        // No llamamos a super.successfulAuthentication porque evitamos que Spring
        // cree una sesión HTTP (la aplicación es stateless y gestiona auth con JWT).
    }
}







