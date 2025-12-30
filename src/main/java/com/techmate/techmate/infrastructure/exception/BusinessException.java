package com.techmate.techmate.infrastructure.exception;

/**
 * Excepción de negocio base para la aplicación.
 * Permite distinguir errores de dominio y llevar códigos/mensajes estructurados.
 */
public class BusinessException extends RuntimeException {
    private final String code;

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}







