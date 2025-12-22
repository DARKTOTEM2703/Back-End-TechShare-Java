// Deprecated placeholder: implementation consolidated into BorrowServiceImpl.java
// This file was retained temporarily during refactor. Use `BorrowServiceImpl.java` instead.
// TODO: remove this file in a future cleanup once all consumers are verified.

package com.techmate.techmate.service.impl;

public class BorrowServiceImplSOLID {

}

    /**
     * Constructor injection para cumplir con DIP.
     * Facilita testing y reduce acoplamiento.
     */
    public BorrowServiceImplSOLID(
            BorrowQueryService queryService,
            BorrowStateProcessor stateProcessor) {
        this.queryService = queryService;
        this.stateProcessor = stateProcessor;
    }

    // ==================== OPERACIONES DE CONSULTA ====================
    /**
     * 📋 Obtiene todos los préstamos.
     * Delega en el servicio de consultas especializado.
     */
    @Override
    public List<BorrowDTO> getAllBorrowDTO() {
        return queryService.getAllBorrows();
    }

    /**
     * 🔍 Obtiene préstamos filtrados por estado.
     * Delega en el servicio de consultas especializado.
     */
    @Override
    public List<BorrowDTO> getBorrowByStatus(String status) {
        return queryService.getBorrowsByStatus(status);
    }

    /**
     * 📅 Obtiene préstamos filtrados por rango de fechas.
     * Delega en el servicio de consultas especializado.
     */
    @Override
    public List<BorrowDTO> getBorrowByDate(Date startDate, Date endDate) {
        return queryService.getBorrowsByDateRange(startDate, endDate);
    }

    // ==================== OPERACIONES DE ESTADO ====================

    /**
     * ⚙️ Actualiza el estado de un préstamo.
     * Delega en el procesador de estados especializado.
     * 
     * @param borrowId  ID del préstamo
     * @param newStatus Nuevo estado
     * @param adminId   ID del administrador
     */
    @Override
    public void updateBorrowStatus(Integer borrowId, Status newStatus, Integer adminId) throws Exception {
        try {
            stateProcessor.processStateTransition(borrowId, newStatus, adminId);
        } catch (RuntimeException e) {
            // Convertir RuntimeException a Exception para mantener compatibilidad con
            // interfaz
            throw new Exception(e.getMessage(), e);
        }
    }

    // ==================== OPERACIONES DE UTILIDAD ====================

    /**
     * 🔐 Extrae el ID de usuario desde un token JWT.
     * Operación de utilidad que no requiere especialización.
     */
    @Override
    public Integer getUserIdFromToken(String token) {
        return TokenUtils.getUserIdFromToken(token);
    }
}
