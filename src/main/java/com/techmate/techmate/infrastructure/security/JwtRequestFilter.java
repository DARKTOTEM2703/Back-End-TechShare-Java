package com.techmate.techmate.infrastructure.security;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filtro JWT para validar tokens en cada petición.
 * 
 * Este es el filtro correcto para Spring Boot 3.x que:
 * 1. Extrae el token JWT del header Authorization
 * 2. Valida el token usando TokenUtils
 * 3. Carga el usuario desde UserDetailsService
 * 4. Establece la autenticación en SecurityContextHolder
 */
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtRequestFilter.class);

    private final UserDetailsService userDetailsService;

    public JwtRequestFilter(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String requestTokenHeader = request.getHeader("Authorization");
        final String requestURI = request.getRequestURI();

        log.debug("🔍 JWT Request Filter - {} {}", request.getMethod(), requestURI);
        log.debug("Authorization header: {}", requestTokenHeader != null ? "Bearer [PRESENTE]" : "null");

        String username = null;
        String jwtToken = null;

        // JWT Token está en la forma "Bearer token". Remover Bearer word y obtener solo
        // el Token
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                username = TokenUtils.getUsernameFromToken(jwtToken);
                log.debug("🔐 Username extraído del token: {}", username);
            } catch (IllegalArgumentException e) {
                log.error("❌ No se puede obtener JWT Token: {}", e.getMessage());
            } catch (Exception e) {
                log.error("❌ JWT Token ha expirado o es inválido: {}", e.getMessage());
            }
        } else {
            log.debug("JWT Token no inicia con Bearer String");
        }

        // Una vez obtenemos el token validamos
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // Si el token es válido configuramos Spring Security para establecer
            // autenticación manualmente
            if (TokenUtils.validateToken(jwtToken, userDetails)) {

                log.info("✅ Token válido para usuario: {}", username);

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, jwtToken, userDetails.getAuthorities());

                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Después de establecer Authentication en el contexto, especificamos
                // que el usuario actual está autenticado. Así pasa las configuraciones de
                // Spring Security exitosamente.
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

                log.info("🔒 Autenticación establecida en SecurityContext para: {}", username);
            } else {
                log.warn("❌ Token inválido para usuario: {}", username);
            }
        }

        filterChain.doFilter(request, response);
    }
}






