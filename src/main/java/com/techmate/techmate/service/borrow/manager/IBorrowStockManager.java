package com.techmate.techmate.service.borrow.manager;

import com.techmate.techmate.entity.Borrow;
import com.techmate.techmate.entity.Materials;
import com.techmate.techmate.entity.Usuario;

/**
 * Contrato para la gestión de stock en operaciones de préstamo.
 */
public interface IBorrowStockManager {
    boolean validateStockAvailability(Integer materialId, int requestedQuantity);

    void reduceStock(Integer materialId, int quantity);

    void restoreStock(Integer materialId, int quantity);

    int getAvailableStock(Integer materialId);

    boolean isMaterialBorrowable(Integer materialId);

    /**
     * Reserva stock y registra movimiento de salida (BORROW) de forma atómica.
     * 
     * @param material Material a reservar
     * @param quantity Cantidad a reservar
     * @param borrow   Préstamo asociado
     * @param usuario  Usuario que realiza el préstamo
     */
    void reserveStockAndLogMovement(Materials material, int quantity, Borrow borrow, Usuario usuario);

    /**
     * Libera stock y registra movimiento de entrada (RETURN) de forma atómica.
     * 
     * @param material Material a liberar
     * @param quantity Cantidad a liberar
     * @param borrow   Préstamo asociado
     * @param usuario  Usuario que devuelve
     */
    void releaseStockAndLogMovement(Materials material, int quantity, Borrow borrow, Usuario usuario);
}
