package com.techmate.techmate.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.techmate.techmate.dto.ApiErrorResponse;

import org.springframework.http.converter.HttpMessageNotReadableException;

import jakarta.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Mapea nombres de campo en snake_case a etiquetas amigables en español
     */
    private static final java.util.Map<String, String> FIELD_LABELS = new java.util.HashMap<String, String>() {{
        put("user_name", "Nombre de usuario");
        put("first_name", "Nombre");
        put("last_name", "Apellido");
        put("email", "Correo electrónico");
        put("password", "Contraseña");
        put("birthDate", "Fecha de nacimiento");
        put("gender", "Género");
        put("roles", "Roles");
    }};

    /**
     * Traduce un nombre de campo a una etiqueta amigable
     */
    private String translateFieldName(String fieldName) {
        return FIELD_LABELS.getOrDefault(fieldName, fieldName);
    }

    @ExceptionHandler(NotFoundException.class)
        public ResponseEntity<ApiErrorResponse> handleNotFound(NotFoundException ex, HttpServletRequest request) {
                log.warn("Recurso no encontrado: {}", ex.getMessage());
        
                ApiErrorResponse body = ApiErrorResponse.builder()
                                .timestamp(java.time.LocalDateTime.now())
                                .status(HttpStatus.NOT_FOUND.value())
                                .error("No encontrado")
                                .message(ex.getMessage())
                                .path(request.getRequestURI())
                                .validationErrors(Collections.emptyList())
                                .build();
        
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
        }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgNotValid(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(fe -> translateFieldName(fe.getField()) + ": " + fe.getDefaultMessage())
                .collect(Collectors.toList());
        
        log.warn("Validación fallida para la petición {}: {}", request.getRequestURI(), errors);
        
        ApiErrorResponse body = ApiErrorResponse.builder()
                .timestamp(java.time.LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Petición inválida")
                .message("Validación fallida")
                .path(request.getRequestURI())
                .code("VALIDATION_ERROR")
                .validationErrors(errors)
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(cv -> translateFieldName(cv.getPropertyPath().toString()) + ": " + cv.getMessage())
                .collect(Collectors.toList());
        
        log.warn("Violación de restricciones en {}: {}", request.getRequestURI(), errors);
        
        ApiErrorResponse body = ApiErrorResponse.builder()
                .timestamp(java.time.LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Petición inválida")
                .message("Validación fallida")
                .path(request.getRequestURI())
                .code("VALIDATION_ERROR")
                .validationErrors(errors)
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ApiErrorResponse> handleInsufficientStock(InsufficientStockException ex, HttpServletRequest request) {
        log.warn("Stock insuficiente: {}", ex.getMessage());
        
        ApiErrorResponse body = ApiErrorResponse.builder()
                .timestamp(java.time.LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .error("Conflicto")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .code("INSUFFICIENT_STOCK")
                .validationErrors(Collections.emptyList())
                .build();
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusiness(BusinessException ex, HttpServletRequest request) {
        log.warn("Excepción de negocio [{}]: {}", ex.getCode(), ex.getMessage());
        
        ApiErrorResponse body = ApiErrorResponse.builder()
                .timestamp(java.time.LocalDateTime.now())
                .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .error("Entidad no procesable")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .code(ex.getCode())
                .validationErrors(Collections.emptyList())
                .build();
        
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(body);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        log.warn("Acceso denegado en {}: {}", request.getRequestURI(), ex.getMessage());
        
        ApiErrorResponse body = ApiErrorResponse.builder()
                .timestamp(java.time.LocalDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error("Prohibido")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .code("ACCESS_DENIED")
                .validationErrors(Collections.emptyList())
                .build();
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthentication(AuthenticationException ex, HttpServletRequest request) {
        log.warn("Autenticación fallida en {}: {}", request.getRequestURI(), ex.getMessage());
        
        ApiErrorResponse body = ApiErrorResponse.builder()
                .timestamp(java.time.LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("No autorizado")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .code("UNAUTHENTICATED")
                .validationErrors(Collections.emptyList())
                .build();
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.warn("JSON malformado en {}: {}", request.getRequestURI(), ex.getMessage());
        
        ApiErrorResponse body = ApiErrorResponse.builder()
                .timestamp(java.time.LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Petición inválida")
                .message("JSON inválido o cuerpo no parseable")
                .path(request.getRequestURI())
                .code("MALFORMED_JSON")
                .validationErrors(Collections.emptyList())
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(org.springframework.web.server.ResponseStatusException.class)
    public ResponseEntity<ApiErrorResponse> handleResponseStatus(org.springframework.web.server.ResponseStatusException ex, HttpServletRequest request) {
        log.warn("Excepción de estado de respuesta [{}] en {}: {}", ex.getStatusCode(), request.getRequestURI(), ex.getReason());
        
        ApiErrorResponse body = ApiErrorResponse.builder()
                .timestamp(java.time.LocalDateTime.now())
                .status(ex.getStatusCode().value())
                .error(ex.getStatusCode().toString())
                .message(ex.getReason() != null ? ex.getReason() : ex.getStatusCode().toString())
                .path(request.getRequestURI())
                .code("RESPONSE_STATUS_ERROR")
                .validationErrors(Collections.emptyList())
                .build();
        
        return ResponseEntity.status(ex.getStatusCode()).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Excepción no manejada en {} {}", request.getMethod(), request.getRequestURI(), ex);
        
        ApiErrorResponse body = ApiErrorResponse.builder()
                .timestamp(java.time.LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Error interno del servidor")
                .message("Ha ocurrido un error interno")
                .path(request.getRequestURI())
                .code("INTERNAL_ERROR")
                .validationErrors(Collections.emptyList())
                .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}


