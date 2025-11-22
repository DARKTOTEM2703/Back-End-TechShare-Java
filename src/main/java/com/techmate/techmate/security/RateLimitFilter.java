package com.techmate.techmate.security;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filtro de Rate Limiting para prevenir abuso de API y ataques de fuerza bruta.
 * Implementa límite de 100 requests por minuto por IP.
 */
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    // Configuración: 100 requests por minuto
    private static final int REQUESTS_PER_MINUTE = 100;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain) throws ServletException, IOException {

        // Excluir endpoints públicos de rate limiting
        String requestURI = request.getRequestURI();
        if (isExcludedPath(requestURI)) {
            chain.doFilter(request, response);
            return;
        }

        String clientIP = getClientIP(request);
        Bucket bucket = resolveBucket(clientIP);

        if (bucket.tryConsume(1)) {
            // Request permitido
            chain.doFilter(request, response);
        } else {
            // Rate limit excedido
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"error\":\"Too Many Requests\",\"message\":\"Rate limit exceeded. Please try again later.\"}");
        }
    }

    /**
     * Resuelve o crea un bucket para la IP del cliente
     */
    private Bucket resolveBucket(String clientIP) {
        return buckets.computeIfAbsent(clientIP, k -> createNewBucket());
    }

    /**
     * Crea un nuevo bucket con límite de 100 requests por minuto
     * Usa la API actualizada de Bucket4j 7.x
     */
    private Bucket createNewBucket() {
        // Usar la API moderna de Bucket4j
        Bandwidth limit = Bandwidth.builder()
                .capacity(REQUESTS_PER_MINUTE)
                .refillIntervally(REQUESTS_PER_MINUTE, Duration.ofMinutes(1))
                .build();
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    /**
     * Obtiene la IP real del cliente considerando proxies
     */
    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIP = request.getHeader("X-Real-IP");
        if (xRealIP != null && !xRealIP.isEmpty()) {
            return xRealIP;
        }

        return request.getRemoteAddr();
    }

    /**
     * Paths excluidos del rate limiting (health checks, actuator)
     */
    private boolean isExcludedPath(String path) {
        return path.startsWith("/actuator/") ||
                path.equals("/") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs");
    }
}
