package com.techmate.techmate.hexagonal.infrastructure.exception;

/**
 * ✅ DIP & ISP - Exception para manejo de tokens inválidos
 * 
 * Excepción específica para tokens JWT inválidos, expirados o mal formados.
 * 
 * @author TechShare Team
 * @version 1.0
 */
public class InvalidTokenException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor con mensaje
     * @param message descripción del error
     */
    public InvalidTokenException(String message) {
        super(message);
    }

    /**
     * Constructor con mensaje y causa
     * @param message descripción del error
     * @param cause causa de la excepción
     */
    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor solo con causa
     * @param cause causa de la excepción
     */
    public InvalidTokenException(Throwable cause) {
        super(cause);
    }

}


