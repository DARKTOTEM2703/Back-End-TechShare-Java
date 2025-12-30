package com.techmate.techmate.hexagonal.infrastructure.service.notification;

/**
 * Interfaz para envío de correos electrónicos.
 * Permite desacoplar la lógica de notificaciones del backend.
 */
public interface EmailService {

    /**
     * Envía correo de confirmación de préstamo.
     * 
     * @param userEmail Email del usuario
     * @param borrowId  ID del préstamo
     * @param materials Lista de materiales en el préstamo
     * @param amount    Monto total
     */
    void sendBorrowConfirmation(String userEmail, Integer borrowId, String materials, java.math.BigDecimal amount);

    /**
     * Envía correo de confirmación de devolución.
     * 
     * @param userEmail  Email del usuario
     * @param borrowId   ID del préstamo
     * @param returnDate Fecha de devolución
     */
    void sendReturnConfirmation(String userEmail, Integer borrowId, String returnDate);

    /**
     * Envía alerta de devolución tardía.
     * 
     * @param userEmail Email del usuario
     * @param borrowId  ID del préstamo
     * @param daysLate  Días de retraso
     * @param penalty   Penalización aplicada
     */
    void sendLateReturnAlert(String userEmail, Integer borrowId, Long daysLate, java.math.BigDecimal penalty);

    /**
     * Envía alerta de stock bajo a administradores.
     * 
     * @param adminEmail   Email del administrador
     * @param materialName Nombre del material
     * @param currentStock Stock actual
     * @param threshold    Umbral mínimo
     */
    void sendLowStockAlert(String adminEmail, String materialName, Integer currentStock, Integer threshold);
}







