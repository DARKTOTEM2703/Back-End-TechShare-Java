package com.techmate.techmate.exception;

/**
 * ✅ ISP - Exception para operaciones inválidas en movimientos
 * 
 * Excepción específica para violaciones de reglas de negocio en movimientos.
 * Por ejemplo: Tipo de movimiento inválido, stock insuficiente, etc.
 * 
 * @author TechShare Team
 * @version 1.0
 */
public class InvalidMovementException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor con mensaje
     * @param message descripción del error
     */
    public InvalidMovementException(String message) {
        super(message);
    }

    /**
     * Constructor con mensaje y causa
     * @param message descripción del error
     * @param cause causa de la excepción
     */
    public InvalidMovementException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor solo con causa
     * @param cause causa de la excepción
     */
    public InvalidMovementException(Throwable cause) {
        super(cause);
    }

}

