package com.techmate.techmate.exception;

/** Excepción para cuando no hay stock suficiente (409 o 422). */
public class InsufficientStockException extends BusinessException {
    public InsufficientStockException(String message) {
        super("INSUFFICIENT_STOCK", message);
    }
    public InsufficientStockException(String message, Throwable cause) {
        super("INSUFFICIENT_STOCK", message, cause);
    }
}


