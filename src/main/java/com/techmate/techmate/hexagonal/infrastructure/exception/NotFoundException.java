package com.techmate.techmate.hexagonal.infrastructure.exception;

/** Excepción lanzada cuando un recurso no es encontrado (404). */
public class NotFoundException extends BusinessException {
    public NotFoundException(String message) {
        super("NOT_FOUND", message);
    }
    public NotFoundException(String message, Throwable cause) {
        super("NOT_FOUND", message, cause);
    }
}



