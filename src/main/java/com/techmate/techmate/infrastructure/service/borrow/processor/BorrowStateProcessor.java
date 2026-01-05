package com.techmate.techmate.infrastructure.service.borrow.processor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import com.techmate.techmate.domain.entity.Borrow;
import com.techmate.techmate.domain.entity.DetailsBorrow;
import com.techmate.techmate.domain.entity.Materials;
import com.techmate.techmate.domain.entity.Status;
import com.techmate.techmate.domain.entity.Usuario;
import com.techmate.techmate.infrastructure.exception.BusinessException;
import com.techmate.techmate.domain.repository.BorrowRepository;
import com.techmate.techmate.domain.repository.UsuarioRepository;
import com.techmate.techmate.infrastructure.service.borrow.manager.BorrowStockManager;
import com.techmate.techmate.infrastructure.service.borrow.validator.BorrowStateValidator;

/**
 * 🎯 Procesador de transiciones de estado para préstamos siguiendo SRP +
 * Strategy Pattern.
 * 
 * PRINCIPIOS SOLID APLICADOS:
 * - SRP: Solo se encarga de procesar transiciones de estado de préstamos
 * - OCP: Extensible para nuevas transiciones sin modificar código existente
 * - LSP: Puede ser sustituido por cualquier implementación del contrato
 * - ISP: Interfaz específica para procesamiento de estados
 * - DIP: Depende de abstracciones (Validator, StockManager) no de
 * implementaciones
 * 
 * STRATEGY PATTERN:
 * - Cada transición de estado tiene su propia estrategia de procesamiento
 * - Fácil agregar nuevas estrategias sin modificar código existente
 * 
 * RESPONSABILIDADES:
 * - Procesar transición a estado REJECTED
 * - Procesar transición a estado BORROWED (con reducción de stock)
 * - Procesar transición a estado RETURNED (con restauración de stock)
 * - Coordinar validaciones y actualizaciones de stock
 * 
 * @author TechShare Team - SOLID Implementation
 */
@Component
public class BorrowStateProcessor {

    private final BorrowRepository borrowRepository;
    private final UsuarioRepository usuarioRepository;
    private final BorrowStateValidator stateValidator;
    private final BorrowStockManager stockManager;

    /**
     * Constructor injection para cumplir con DIP.
     */
    public BorrowStateProcessor(
            BorrowRepository borrowRepository,
            UsuarioRepository usuarioRepository,
            BorrowStateValidator stateValidator,
            BorrowStockManager stockManager) {
        this.borrowRepository = borrowRepository;
        this.usuarioRepository = usuarioRepository;
        this.stateValidator = stateValidator;
        this.stockManager = stockManager;
    }

    /**
     * Procesa la transición de estado de un préstamo.
     * Utiliza Strategy Pattern para delegar a la estrategia específica.
     * 
     * @param borrowId  ID del préstamo
     * @param newStatus Nuevo estado
     * @param adminId   ID del administrador que procesa
     * @throws BusinessException si la transición no es válida
     */
    @Transactional
    public void processStateTransition(Integer borrowId, Status newStatus, Integer adminId) {

        // Obtener préstamo
        Borrow borrow = borrowRepository.findById(borrowId)
                .orElseThrow(() -> new BusinessException(
                        "BORROW_NOT_FOUND", String.format("Préstamo no encontrado con ID: %d", borrowId)));

        // Validar transición
        stateValidator.validateStateTransition(borrow.getStatus(), newStatus);

        // Obtener y asignar administrador
        Usuario admin = usuarioRepository.findById(adminId)
                .orElseThrow(() -> new BusinessException(
                        "USER_NOT_FOUND", String.format("Administrador no encontrado con ID: %d", adminId)));
        borrow.setAdmin(admin);

        // Aplicar estrategia específica según el nuevo estado
        switch (newStatus) {
            case REJECTED:
                processRejection(borrow);
                break;

            case BORROWED:
                processBorrowing(borrow);
                break;

            case RETURNED:
                processReturn(borrow);
                break;

            default:
                throw new BusinessException("INVALID_BORROW_STATUS",
                        "Estado no soportado para transición: " + newStatus);
        }

        // Guardar cambios
        borrowRepository.save(borrow);
    }

    // ==================== ESTRATEGIAS DE PROCESAMIENTO ====================

    /**
     * Estrategia para procesar rechazo de préstamo.
     * 
     * @param borrow Préstamo a rechazar
     */
    private void processRejection(Borrow borrow) {
        borrow.setStatus(Status.REJECTED);
        borrow.setEndDate(new Date());

        // No se requiere gestión de stock en rechazo
        // El stock nunca fue reducido
    }

    /**
     * Estrategia para procesar aprobación de préstamo.
     * Incluye validación y reducción de stock.
     * 
     * @param borrow Préstamo a aprobar
     */
    private void processBorrowing(Borrow borrow) {

        // Validar y reservar stock + registrar movimiento para cada detalle
        for (DetailsBorrow detail : borrow.getDetails()) {
            int quantity = detail.getQuantity();
            Materials material = detail.getMaterials();

            // Reserva stock y registra movimiento (método transaccional MANDATORY)
            stockManager.reserveStockAndLogMovement(material, quantity, borrow, borrow.getUsuario());
        }

        // Actualizar estado y fechas
        borrow.setStatus(Status.BORROWED);
        borrow.setStartDate(new Date());
    }

    /**
     * Estrategia para procesar devolución de préstamo.
     * Incluye restauración de stock.
     * 
     * @param borrow Préstamo a devolver
     */
    private void processReturn(Borrow borrow) {

        // Restaurar stock y registrar movimiento para cada detalle
        for (DetailsBorrow detail : borrow.getDetails()) {
            int quantity = detail.getQuantity();
            Materials material = detail.getMaterials();

            stockManager.releaseStockAndLogMovement(material, quantity, borrow, borrow.getUsuario());
        }

        // Actualizar estado y fechas
        borrow.setStatus(Status.RETURNED);
        borrow.setReturnDate(new Date());
        borrow.setEndDate(new Date());
    }
}
