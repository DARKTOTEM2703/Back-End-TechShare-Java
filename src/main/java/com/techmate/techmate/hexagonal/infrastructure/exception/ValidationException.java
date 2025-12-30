package com.techmate.techmate.hexagonal.infrastructure.exception;

/** Excepción para errores de validación (400). */
public class ValidationException extends BusinessException {
    public ValidationException(String message) {
        super("VALIDATION_ERROR", message);
    }
    public ValidationException(String message, Throwable cause) {
        super("VALIDATION_ERROR", message, cause);
    }
}







