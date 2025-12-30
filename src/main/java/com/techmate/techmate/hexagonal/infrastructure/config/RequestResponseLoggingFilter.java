package com.techmate.techmate.hexagonal.infrastructure.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Filtro para loguear todas las peticiones HTTP y sus respuestas.
 * Incluye request ID único para trazabilidad.
 */
@Component
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);
    private static final String REQUEST_ID = "requestId";

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // Generar ID único para la petición
        String requestId = UUID.randomUUID().toString();
        MDC.put(REQUEST_ID, requestId);

        // Envolver request y response para poder leer el body múltiples veces
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        long startTime = System.currentTimeMillis();

        try {
            // Log request
            logRequest(wrappedRequest, requestId);

            // Continuar con la cadena de filtros
            filterChain.doFilter(wrappedRequest, wrappedResponse);

            // Log response
            logResponse(wrappedRequest, wrappedResponse, System.currentTimeMillis() - startTime);

        } finally {
            // Copiar el body de la respuesta al cliente
            wrappedResponse.copyBodyToResponse();
            // Limpiar MDC
            MDC.remove(REQUEST_ID);
        }
    }

    private void logRequest(ContentCachingRequestWrapper request, String requestId) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();

        StringBuilder logMessage = new StringBuilder();
        logMessage.append(String.format("→ Incoming Request [%s] %s %s", requestId, method, uri));

        if (queryString != null) {
            logMessage.append("?").append(queryString);
        }

        // Log headers (solo algunos importantes)
        String contentType = request.getContentType();
        if (contentType != null) {
            logMessage.append(" | Content-Type: ").append(contentType);
        }

        String userAgent = request.getHeader("User-Agent");
        if (userAgent != null) {
            logMessage.append(" | User-Agent: ").append(userAgent.substring(0, Math.min(50, userAgent.length())));
        }

        log.info(logMessage.toString());

        // Log request body solo para POST/PUT/PATCH (y no muy grande)
        if (shouldLogBody(method) && request.getContentLength() > 0 && request.getContentLength() < 1000) {
            byte[] content = request.getContentAsByteArray();
            if (content.length > 0) {
                String body = new String(content, StandardCharsets.UTF_8);
                // No loguear el body si contiene "password" para seguridad
                if (!body.toLowerCase().contains("password")) {
                    log.debug("Request Body: {}", body);
                } else {
                    log.debug("Request Body: [REDACTED - contains sensitive data]");
                }
            }
        }
    }

    private void logResponse(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response,
            long duration) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        int status = response.getStatus();

        String statusEmoji = getStatusEmoji(status);

        String logMessage = String.format("← Response %s %s %s | Status: %d | Duration: %dms",
                statusEmoji, method, uri, status, duration);

        if (status >= 500) {
            log.error(logMessage);
        } else if (status >= 400) {
            log.warn(logMessage);
        } else {
            log.info(logMessage);
        }

        // Log response body solo en caso de error (y no muy grande)
        if (status >= 400 && response.getContentSize() > 0 && response.getContentSize() < 1000) {
            byte[] content = response.getContentAsByteArray();
            if (content.length > 0) {
                String body = new String(content, StandardCharsets.UTF_8);
                log.debug("Response Body: {}", body);
            }
        }
    }

    private boolean shouldLogBody(String method) {
        return "POST".equals(method) || "PUT".equals(method) || "PATCH".equals(method);
    }

    private String getStatusEmoji(int status) {
        if (status >= 500)
            return "❌";
        if (status >= 400)
            return "⚠️";
        if (status >= 300)
            return "↪️";
        if (status >= 200)
            return "✅";
        return "ℹ️";
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        // No filtrar endpoints de Actuator para reducir noise
        String path = request.getRequestURI();
        return path.startsWith("/actuator/health") ||
                path.startsWith("/actuator/prometheus") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/api-docs");
    }
}


