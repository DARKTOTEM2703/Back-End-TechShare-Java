package com.techmate.techmate.service.impl;

import com.techmate.techmate.dto.BorrowDTO;
import com.techmate.techmate.entity.Status;
import com.techmate.techmate.security.TokenUtils;
import com.techmate.techmate.service.BorrowService;
import com.techmate.techmate.service.borrow.processor.BorrowStateProcessor;
import com.techmate.techmate.service.borrow.query.BorrowQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 🎯 Implementación del servicio de préstamos (Facade).
 * * Esta clase actúa como un COORDINADOR (Facade Pattern).
 * No contiene lógica de negocio compleja, solo delega responsabilidades a:
 * 1. BorrowQueryService -> Para lecturas y búsquedas.
 * 2. BorrowStateProcessor -> Para cambios de estado y escritura.
 * 3. TokenUtils -> Para utilidades de seguridad.
 * * @author TechShare Team
 */
@Slf4j
@Service
@Primary
@RequiredArgsConstructor // Lombok genera el constructor automáticamente (más limpio)
public class BorrowServiceImpl implements BorrowService {

    private final BorrowQueryService queryService;
    private final BorrowStateProcessor stateProcessor;

    // ==================== OPERACIONES DE CONSULTA ====================

    /**
     * 📋 Obtiene todos los préstamos.
     */
    @Override
    public List<BorrowDTO> getAllBorrowDTO() {
        return queryService.getAllBorrows();
    }

    /**
     * 🔍 Obtiene préstamos filtrados por estado.
     */
    @Override
    public List<BorrowDTO> getBorrowByStatus(String status) {
        return queryService.getBorrowsByStatus(status);
    }

    /**
     * 📅 Obtiene préstamos filtrados por rango de fechas.
     */
    @Override
    public List<BorrowDTO> getBorrowByDate(Date startDate, Date endDate) {
        return queryService.getBorrowsByDateRange(startDate, endDate);
    }

    // ==================== OPERACIONES DE ESTADO (ESCRITURA) ====================

    /**
     * ⚙️ Actualiza el estado de un préstamo.
     * Delega la complejidad de la máquina de estados, validación de stock
     * y eventos al StateProcessor.
     */
    @Override
    public void updateBorrowStatus(Integer borrowId, Status newStatus, Integer adminId) throws Exception {
        log.info("Solicitud de cambio de estado para Borrow ID: {} a {}", borrowId, newStatus);
        try {
            stateProcessor.processStateTransition(borrowId, newStatus, adminId);
        } catch (RuntimeException e) {
            log.error("Error procesando transición de estado: {}", e.getMessage());
            // Relanzamos como Exception genérica para respetar la firma de la interfaz
            // (aunque deberíamos refactorizar la interfaz en el futuro)
            throw new Exception(e.getMessage(), e);
        }
    }

    // ==================== OPERACIONES DE UTILIDAD ====================

    /**
     * 🔐 Extrae el ID de usuario desde un token JWT.
     */
    @Override
    public Integer getUserIdFromToken(String token) {
        return TokenUtils.getUserIdFromToken(token);
    }
}