package com.techmate.techmate.infrastructure.exception;

/**
 * Excepciones de dominio específicas para operaciones de préstamo.
 */
public class BorrowBusinessException extends BusinessException {

    public BorrowBusinessException(String code, String message) {
        super(code, message);
    }

    public BorrowBusinessException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }

    public static BorrowBusinessException materialNotFound(Integer id) {
        return new BorrowBusinessException("BORROW_MATERIAL_NOT_FOUND",
                String.format("Material no encontrado con ID: %d", id));
    }

    public static BorrowBusinessException insufficientStock(Integer id, int requested, int available) {
        return new BorrowBusinessException("BORROW_INSUFFICIENT_STOCK",
                String.format("Stock insuficiente para el material ID %d. Solicitado: %d, Disponible: %d", id,
                        requested, available));
    }
}







