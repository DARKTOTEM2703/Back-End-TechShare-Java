package com.techmate.techmate.service.borrow.manager;

/**
 * Contrato para la gestión de stock en operaciones de préstamo.
 */
public interface IBorrowStockManager {
    boolean validateStockAvailability(Integer materialId, int requestedQuantity);
    void reduceStock(Integer materialId, int quantity);
    void restoreStock(Integer materialId, int quantity);
    int getAvailableStock(Integer materialId);
    boolean isMaterialBorrowable(Integer materialId);
}


