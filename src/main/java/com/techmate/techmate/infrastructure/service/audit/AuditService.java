package com.techmate.techmate.infrastructure.service.audit;

/**
 * Servicio para registrar eventos de auditoría.
 * Mantiene un historial completo de operaciones críticas.
 */
public interface AuditService {
    
    /**
     * Registra un nuevo evento de auditoría.
     * @param action Acción realizada (CREATE, UPDATE, DELETE, etc.)
     * @param entity Tipo de entidad (Borrow, Material, Usuario, etc.)
     * @param entityId ID de la entidad
     * @param details Detalles adicionales
     * @param userId ID del usuario que realizó la acción
     */
    void logAudit(String action, String entity, Integer entityId, String details, Integer userId);
    
    /**
     * Registra específicamente eventos de préstamo.
     */
    void logBorrow(Integer borrowId, String action, Integer userId, String details);
    
    /**
     * Registra específicamente eventos de materiales.
     */
    void logMaterial(Integer materialId, String action, String details);
}








