package com.techmate.techmate.hexagonal.infrastructure.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Filter que añade un Request ID único a cada petición HTTP.
 * Este ID se propaga a través del MDC de SLF4J para logging estructurado.
 * 
 * El Request ID:
 * - Se genera automáticamente si no viene en headers
 * - Se añade al MDC para aparecer en todos los logs de la petición
 * - Se devuelve en la respuesta para correlación
 * - Permite rastrear una petición completa a través de todos los logs
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestIdFilter extends OncePerRequestFilter {

    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    private static final String REQUEST_ID_MDC_KEY = "requestId";
    private static final String IP_MDC_KEY = "clientIp";

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        try {
            // 1. Obtener o generar Request ID
            String requestId = request.getHeader(REQUEST_ID_HEADER);
            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            // 2. Añadir Request ID al MDC (Mapped Diagnostic Context)
            MDC.put(REQUEST_ID_MDC_KEY, requestId);

            // 3. Añadir IP del cliente al MDC
            String clientIp = getClientIp(request);
            MDC.put(IP_MDC_KEY, clientIp);

            // 4. Añadir usuario autenticado al MDC (si existe)
            // Nota: El usuario se añadirá después de la autenticación en
            // JwtAuthenticationFilter

            // 5. Añadir Request ID a la respuesta para correlación
            response.setHeader(REQUEST_ID_HEADER, requestId);

            // 6. Continuar con la cadena de filtros
            filterChain.doFilter(request, response);

        } finally {
            // 7. Limpiar MDC al finalizar la petición (importante para evitar memory leaks)
            MDC.clear();
        }
    }

    /**
     * Obtiene la IP real del cliente considerando proxies y load balancers.
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // X-Forwarded-For puede contener múltiples IPs, la primera es la del cliente
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}


