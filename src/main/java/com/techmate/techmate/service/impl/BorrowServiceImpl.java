package com.techmate.techmate.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Primary;

import java.util.*;

import com.techmate.techmate.service.BorrowService;
import com.techmate.techmate.service.borrow.processor.BorrowStateProcessor;
import com.techmate.techmate.service.borrow.query.BorrowQueryService;
import com.techmate.techmate.dto.BorrowDTO;
import com.techmate.techmate.entity.Status;
import com.techmate.techmate.security.TokenUtils;

/**
 * 🎯 Implementación del servicio de préstamos refactorizada con principios
 * SOLID.
 * 
 * PRINCIPIOS SOLID APLICADOS:
 * - SRP: Solo coordina operaciones, delega responsabilidades específicas
 * - OCP: Extensible sin modificar código existente
 * - LSP: Implementa correctamente la interfaz BorrowService
 * - ISP: Depende solo de interfaces necesarias
 * - DIP: Depende de abstracciones, no de implementaciones concretas
 * 
 * ARQUITECTURA:
 * - BorrowQueryService: Consultas y filtros
 * - BorrowStateProcessor: Transiciones de estado
 * 
 * @author TechShare Team - Refactorizado con SOLID
 */
@Service
@Primary
public class BorrowServiceImpl implements BorrowService {

    private final BorrowQueryService queryService;
    private final BorrowStateProcessor stateProcessor;

    // ==================== CONSTRUCTOR INJECTION (DIP) ====================

    /**
     * Constructor injection para cumplir con DIP.
     * Facilita testing y reduce acoplamiento.
     */
    public BorrowServiceImpl(
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
 
