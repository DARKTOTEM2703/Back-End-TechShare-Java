package com.techmate.techmate.hexagonal.infrastructure.service.impl;

import com.techmate.techmate.hexagonal.infrastructure.dto.BorrowDTO;
import com.techmate.techmate.hexagonal.domain.entity.Status;
import com.techmate.techmate.hexagonal.infrastructure.security.TokenUtils;
import com.techmate.techmate.hexagonal.infrastructure.service.BorrowService;
import com.techmate.techmate.hexagonal.infrastructure.service.borrow.processor.BorrowStateProcessor;
import com.techmate.techmate.hexagonal.infrastructure.service.borrow.query.BorrowQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 🎯 Fachada para todas las operaciones sobre préstamos.
 * 
 * PATRÓN ARQUITECTÓNICO: Facade + Delegación
 * 
 * PRINCIPIOS SOLID APLICADOS:
 * - SRP: Coordina préstamos, no implementa lógica directa
 * - OCP: Extensible agregando nuevos métodos de coordinación
 * - LSP: Implementa el contrato BorrowService sin violar expectativas
 * - ISP: Interfaz específica para operaciones administrativas de préstamos
 * - DIP: Depende de abstracciones (QueryService, StateProcessor), no de
 * implementaciones
 * 
 * RESPONSABILIDADES PRINCIPALES:
 * - Coordinar consultas a través de BorrowQueryService
 * - Coordinar cambios de estado a través de BorrowStateProcessor
 * - Extraer información del JWT para contexto de seguridad
 * - Validar autorización y contexto antes de operaciones
 * 
 * PATRÓN DE DELEGACIÓN:
 * BorrowServiceImpl (Facade)
 * ├─ Lectura: BorrowQueryService (read-only)
 * ├─ Escritura: BorrowStateProcessor (transaccional)
 * └─ Seguridad: TokenUtils (JWT parsing)
 * 
 * NOTA IMPORTANTE SOBRE CREACIÓN DE PRÉSTAMOS:
 * La creación de préstamos (createBorrowDTO) está en BorrowUserServiceImpl
 * porque:
 * - Es una operación diferente (usuario solicitando) vs. admin gestionando
 * - Tiene validaciones especiales (roles, stock disponible)
 * - BorrowService maneja solo administración de préstamos ya creados
 * 
 * @author TechShare Team - SOLID Implementation
 * @version 2.0
 */
@Slf4j
@Service
@Primary
@RequiredArgsConstructor
@Transactional
public class BorrowServiceImpl implements BorrowService {

    // ==================== DEPENDENCIAS ====================

    /**
     * Servicio especializado en consultas de préstamos (read-only).
     * Implementa el patrón Query Service para separación de lecturas.
     */
    private final BorrowQueryService queryService;

    /**
     * Procesador de cambios de estado para préstamos.
     * Encapsula la máquina de estados y validaciones.
     */
    private final BorrowStateProcessor stateProcessor;

    // ==================== OPERACIONES DE LECTURA ====================

    /**
     * Obtiene todos los préstamos registrados en el sistema.
     * 
     * DELEGACIÓN: BorrowQueryService.getAllBorrows()
     * 
     * @return Lista completa de préstamos como DTO
     */
    @Override
    @Transactional(readOnly = true)
    public List<BorrowDTO> getAllBorrowDTO() {
        log.debug("Obteniendo todos los préstamos del sistema");
        return queryService.getAllBorrows();
    }

    /**
     * Obtiene préstamos filtrados por estado.
     * 
     * DELEGACIÓN: BorrowQueryService.getBorrowsByStatus()
     * 
     * @param status Estado a filtrar (case-insensitive: PENDING, REJECTED,
     *               BORROWED, RETURNED)
     * @return Préstamos con el estado especificado
     * @throws IllegalArgumentException si el estado es inválido
     */
    @Override
    @Transactional(readOnly = true)
    public List<BorrowDTO> getBorrowByStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            log.warn("Intento de obtener préstamos sin especificar estado");
            throw new IllegalArgumentException("El estado es requerido");
        }

        log.debug("Filtrando préstamos por estado: {}", status);
        return queryService.getBorrowsByStatus(status);
    }

    /**
     * Obtiene préstamos dentro de un rango de fechas.
     * 
     * DELEGACIÓN: BorrowQueryService.getBorrowsByDateRange()
     * 
     * @param startDate Fecha de inicio (inclusive)
     * @param endDate   Fecha de fin (inclusive)
     * @return Préstamos creados en el rango especificado
     * @throws IllegalArgumentException si las fechas son inválidas
     */
    @Override
    @Transactional(readOnly = true)
    public List<BorrowDTO> getBorrowByDate(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            log.warn("Intento de obtener préstamos sin especificar rango de fechas");
            throw new IllegalArgumentException("Las fechas de inicio y fin son requeridas");
        }

        log.debug("Filtrando préstamos entre {} y {}", startDate, endDate);
        return queryService.getBorrowsByDateRange(startDate, endDate);
    }

    // ==================== OPERACIONES DE ESCRITURA ====================

    /**
     * Actualiza el estado de un préstamo existente.
     * 
     * DELEGACIÓN: BorrowStateProcessor.processStateTransition()
     * 
     * TRANSACCIONALIDAD:
     * - Operación WRITE (@Transactional sin readOnly)
     * - Todos los cambios se persisten atomáticamente
     * - Si algo falla, todo se revierte (rollback automático)
     * 
     * CAMBIOS DE ESTADO PERMITIDOS:
     * - PENDING → REJECTED (admin rechaza solicitud)
     * - PENDING → BORROWED (admin aprueba y registra préstamo)
     * - BORROWED → RETURNED (usuario devuelve material)
     * 
     * OPERACIONES CRÍTICAS:
     * - Rechazo: Solo marca estado, SIN afectar stock
     * - Préstamo: Valida stock, REDUCE stock disponible por cada item
     * - Devolución: RESTAURA stock disponible por cada item
     * 
     * @param borrowId  ID del préstamo a actualizar
     * @param newStatus Nuevo estado (PENDING, REJECTED, BORROWED, RETURNED)
     * @param adminId   ID del usuario que realiza la operación (para auditoría)
     * @throws Exception si hay errores en la transición
     */
    @Override
    public void updateBorrowStatus(Integer borrowId, Status newStatus, Integer adminId) throws Exception {
        if (borrowId == null || borrowId <= 0) {
            log.error("Intento de actualizar préstamo con ID inválido: {}", borrowId);
            throw new IllegalArgumentException("ID del préstamo inválido");
        }

        if (newStatus == null) {
            log.error("Intento de actualizar préstamo sin especificar nuevo estado");
            throw new IllegalArgumentException("El nuevo estado es requerido");
        }

        log.info("Procesando cambio de estado para préstamo ID: {} → {} (Admin: {})",
                borrowId, newStatus, adminId);

        try {
            // Delegación al procesador de estado que maneja la máquina de estados completa
            stateProcessor.processStateTransition(borrowId, newStatus, adminId);

            log.info("Cambio de estado completado exitosamente para préstamo ID: {}", borrowId);
        } catch (Exception e) {
            log.error("Error al procesar cambio de estado para préstamo ID: {}", borrowId, e);
            throw e;
        }
    }

    // ==================== OPERACIONES DE SEGURIDAD ====================

    /**
     * Extrae el ID de usuario desde el token JWT.
     * 
     * DELEGACIÓN: TokenUtils.getUserIdFromToken()
     * 
     * CONTEXTO DE USO:
     * - Operaciones REST que requieren contexto de usuario autenticado
     * - Auditoría de quién hizo qué cambio
     * - Validación de autorización
     * 
     * @param token JWT token del cliente
     * @return ID del usuario extraído del token
     * @throws RuntimeException si el token es inválido
     */
    @Override
    @Transactional(readOnly = true)
    public Integer getUserIdFromToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            log.warn("Intento de extraer usuario de token vacío");
            throw new IllegalArgumentException("El token es requerido");
        }

        try {
            Integer userId = TokenUtils.getUserIdFromToken(token);
            log.debug("Usuario ID {} extraído del token exitosamente", userId);
            return userId;
        } catch (Exception e) {
            log.error("Error al extraer usuario del token", e);
            throw new RuntimeException("Token inválido: " + e.getMessage(), e);
        }
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Obtiene un préstamo específico por ID.
     * Método auxiliar que no está en la interfaz BorrowService.
     * Útil para operaciones internas.
     * 
     * @param borrowId ID del préstamo
     * @return Optional de DTO del préstamo
     */
    @Transactional(readOnly = true)
    public Optional<BorrowDTO> findBorrowById(Integer borrowId) {
        return queryService.findBorrowById(borrowId);
    }

    /**
     * Obtiene préstamos activos (PENDING + BORROWED).
     * Método auxiliar para filtros comunes.
     * 
     * @return Lista de préstamos activos
     */
    @Transactional(readOnly = true)
    public List<BorrowDTO> getActiveBorrows() {
        log.debug("Obteniendo préstamos activos (PENDING + BORROWED)");
        return queryService.getActiveBorrows();
    }

    /**
     * Obtiene préstamos de un usuario específico.
     * Método auxiliar para consultas por usuario.
     * 
     * @param userId ID del usuario
     * @return Préstamos del usuario
     */
    @Transactional(readOnly = true)
    public List<BorrowDTO> getBorrowsByUser(Integer userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("ID de usuario inválido");
        }

        log.debug("Obteniendo préstamos del usuario ID: {}", userId);
        return queryService.getBorrowsByUser(userId);
    }
}






