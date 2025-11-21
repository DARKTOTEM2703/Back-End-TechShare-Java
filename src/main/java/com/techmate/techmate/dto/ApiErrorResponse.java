package com.techmate.techmate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * DTO estandarizado para respuestas de error del API.
 * 
 * Proporciona un formato consistente para todos los errores,
 * mejorando la experiencia del cliente y facilitando el debugging.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrorResponse {
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    
    private int status;
    private String error;
    private String message;
    private String path;
    private String code; // business code opcional
    private List<String> validationErrors;

    /**
     * Crea una respuesta de error simple.
     */
    public static ApiErrorResponse of(HttpStatus status, String message, String path) {
        return ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .validationErrors(List.of())
                .build();
    }
    
    /**
     * Crea una respuesta de error con código de negocio.
     */
    public static ApiErrorResponse of(int status, String path, String code, List<String> errors) {
        return ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .path(path)
                .code(code)
                .validationErrors(errors)
                .build();
    }
    
    /**
     * Crea una respuesta con errores de validación.
     */
    public static ApiErrorResponse withValidations(
            HttpStatus status, 
            String message, 
            String path, 
            List<String> errors) {
        return ApiErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(status.value())
            .error(status.getReasonPhrase())
            .message(message)
            .path(path)
            .validationErrors(errors)
            .build();
    }
}

