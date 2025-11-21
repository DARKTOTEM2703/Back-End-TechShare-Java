package com.techmate.techmate.service.borrow.validator;

import org.springframework.stereotype.Component;

import com.techmate.techmate.entity.Status;

/**
 * 🎯 Validador de transiciones de estado para préstamos siguiendo SRP.
 * 
 * PRINCIPIOS SOLID APLICADOS:
 * - SRP: Solo se encarga de validar transiciones de estado de préstamos
 * - OCP: Extensible para nuevas reglas de validación sin modificar código existente
 * - LSP: Cualquier implementación debe cumplir el mismo contrato
 * - ISP: Interfaz específica para validaciones de estado
 * - DIP: Puede ser inyectado como dependencia
 * 
 * REGLAS DE NEGOCIO:
 * - PROCESS → BORROWED (con validación de stock)
 * - PROCESS → REJECTED (sin restricciones)
 * - BORROWED → RETURNED (con restauración de stock)
 * - No se permiten otras transiciones
 * 
 * @author TechShare Team - SOLID Implementation
 */
@Component
public class BorrowStateValidator {
    
    /**
     * Valida si la transición de estado es permitida.
     * 
     * @param currentStatus Estado actual del préstamo
     * @param newStatus Nuevo estado solicitado
     * @return true si la transición es válida
     * @throws IllegalArgumentException si la transición no es válida
     */
    public boolean validateStateTransition(Status currentStatus, Status newStatus) {
        
        // Validar que los estados no sean null
        if (currentStatus == null || newStatus == null) {
            throw new IllegalArgumentException("Los estados no pueden ser nulos");
        }
        
        // Si el estado es el mismo, no hay transición
        if (currentStatus == newStatus) {
            return true;
        }
        
        // Aplicar reglas de transición según estado actual
        switch (currentStatus) {
            case PENDING:
                return validateFromPending(newStatus);
                
            case BORROWED:
                return validateFromLoaned(newStatus);
                
            case REJECTED:
            case RETURNED:
                throw new IllegalArgumentException(
                    String.format("No se puede cambiar el estado desde %s a %s. Estado final alcanzado.", 
                        currentStatus, newStatus));
                
            default:
                throw new IllegalArgumentException("Estado actual no reconocido: " + currentStatus);
        }
    }
    
    /**
     * Valida si se puede rechazar un préstamo.
     * 
     * @param currentStatus Estado actual
     * @return true si se puede rechazar
     */
    public boolean validateCanReject(Status currentStatus) {
        return currentStatus == Status.PENDING;
    }
    
    /**
     * Valida si se puede aprobar/prestar un material.
     * 
     * @param currentStatus Estado actual
     * @return true si se puede aprobar
     */
    public boolean validateCanBorrow(Status currentStatus) {
        return currentStatus == Status.PENDING;
    }
    
    /**
     * Valida si se puede devolver un préstamo.
     * 
     * @param currentStatus Estado actual
     * @return true si se puede devolver
     */
    public boolean validateCanReturn(Status currentStatus) {
        return currentStatus == Status.BORROWED;
    }
    
    // ==================== MÉTODOS PRIVADOS ====================
    
    /**
     * Valida transiciones desde estado PROCESS.
     */
    private boolean validateFromPending(Status newStatus) {
        switch (newStatus) {
            case BORROWED:
            case REJECTED:
                return true;
            default:
                throw new IllegalArgumentException(
                    String.format("Transición inválida de PENDING a %s. Solo se permite BORROWED o REJECTED.", 
                        newStatus));
        }
    }
    
    /**
     * Valida transiciones desde estado BORROWED.
     */
    private boolean validateFromLoaned(Status newStatus) {
        switch (newStatus) {
            case RETURNED:
                return true;
            default:
                throw new IllegalArgumentException(
                    String.format("Transición inválida de BORROWED a %s. Solo se permite RETURNED.", 
                        newStatus));
        }
    }
}

