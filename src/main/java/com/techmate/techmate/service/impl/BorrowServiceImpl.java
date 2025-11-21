package com.techmate.techmate.service.impl;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import com.techmate.techmate.dto.BorrowDTO;
import com.techmate.techmate.dto.DetailsBorrowDTO;
import com.techmate.techmate.entity.Borrow;
import com.techmate.techmate.entity.DetailsBorrow;
import com.techmate.techmate.entity.Materials;
import com.techmate.techmate.entity.Status;
import com.techmate.techmate.entity.Usuario;
import com.techmate.techmate.event.BorrowCreatedEvent;
import com.techmate.techmate.event.BorrowReturnedEvent;
import com.techmate.techmate.exception.BorrowBusinessException;
import com.techmate.techmate.exception.BusinessException;
import com.techmate.techmate.repository.BorrowRepository;
import com.techmate.techmate.repository.MaterialsRepository;
import com.techmate.techmate.repository.UsuarioRepository;
import com.techmate.techmate.security.TokenUtils;
import com.techmate.techmate.service.BorrowService;
import com.techmate.techmate.service.borrow.manager.IBorrowStockManager;

@Service
public class BorrowServiceImpl implements BorrowService {
    private final BorrowRepository borrowRepository;
    private final MaterialsRepository materialsRepository;
    private final UsuarioRepository usuarioRepository;
    private final IBorrowStockManager borrowStockManager;
    private final ApplicationEventPublisher eventPublisher;

    public BorrowServiceImpl(BorrowRepository borrowRepository, MaterialsRepository materialsRepository,
            UsuarioRepository usuarioRepository,
            IBorrowStockManager borrowStockManager,
            ApplicationEventPublisher eventPublisher) {
        this.borrowRepository = borrowRepository;
        this.materialsRepository = materialsRepository;
        this.usuarioRepository = usuarioRepository;
        this.borrowStockManager = borrowStockManager;
        this.eventPublisher = eventPublisher;
    }

    // ==================== FALLBACK HELPERS (compatibilidad con tests) ====================
    /**
     * Valida disponibilidad de stock usando IBorrowStockManager si existe;
     * en caso contrario aplica la lógica directa con el repositorio (fallback para tests).
     */
    private void validateStockAvailabilityOrFallback(Integer materialId, int requestedQuantity) {
        if (borrowStockManager != null) {
            borrowStockManager.validateStockAvailability(materialId, requestedQuantity);
            return;
        }

        Materials material = materialsRepository.findById(materialId)
                .orElseThrow(() -> BorrowBusinessException.materialNotFound(materialId));
        if (material.getBorrowable_stock() < requestedQuantity) {
            throw BorrowBusinessException.insufficientStock(materialId, requestedQuantity, material.getBorrowable_stock());
        }
    }

    private void reduceStockOrFallback(Integer materialId, int quantity) {
        if (borrowStockManager != null) {
            borrowStockManager.reduceStock(materialId, quantity);
            return;
        }

        Materials material = materialsRepository.findById(materialId)
                .orElseThrow(() -> BorrowBusinessException.materialNotFound(materialId));

        if (material.getBorrowable_stock() < quantity) {
            throw BorrowBusinessException.insufficientStock(materialId, quantity, material.getBorrowable_stock());
        }

        material.setBorrowable_stock(material.getBorrowable_stock() - quantity);
        materialsRepository.save(material);
    }

    private void restoreStockOrFallback(Integer materialId, int quantity) {
        if (borrowStockManager != null) {
            borrowStockManager.restoreStock(materialId, quantity);
            return;
        }

        Materials material = materialsRepository.findById(materialId)
                .orElseThrow(() -> BorrowBusinessException.materialNotFound(materialId));
        material.setBorrowable_stock(material.getBorrowable_stock() + quantity);
        materialsRepository.save(material);
    }

    private BorrowDTO convertToDTO(Borrow borrow) {
        BorrowDTO dto = new BorrowDTO();
        dto.setId(borrow.getId());
        dto.setDate(borrow.getDate());
        dto.setStatus(borrow.getStatus());
        dto.setAmount(borrow.getAmount());
        dto.setStartDate(borrow.getStartDate());
        dto.setEndDate(borrow.getEndDate());
        dto.setReturnDate(borrow.getReturnDate());;
    
        // Asegúrate de que el usuario y admin se asignen correctamente
        if (borrow.getUsuario() != null) {
            dto.setUsuarioId(borrow.getUsuario().getId());
            dto.setUsuarioName(borrow.getUsuario().getUser_name());
        }
    
        if (borrow.getAdmin() != null) {
            dto.setAdminId(borrow.getAdmin().getId());
            dto.setAdminName(borrow.getAdmin().getUser_name());
        }
    
        // Mapear los detalles del préstamo
        dto.setDetails(borrow.getDetails().stream()
                .map(this::convertDetailsBorrowToDTO)
                .collect(Collectors.toList()));
    
        return dto;
    }
    


    private DetailsBorrowDTO convertDetailsBorrowToDTO(DetailsBorrow detailsBorrow) {
        DetailsBorrowDTO dto = new DetailsBorrowDTO();
        dto.setId(detailsBorrow.getId());

        if (detailsBorrow.getMaterials() != null) {
            dto.setId(detailsBorrow.getMaterials().getId());
        } else {
            dto.setId(null); // O lanzar una excepción si es necesario
        }

        // Establecer el borrowId en el DTO
        if (detailsBorrow.getBorrow() != null) {
            dto.setId(detailsBorrow.getBorrow().getId()); // Aquí estableces el borrowId en el DTO
        }

        dto.setQuantity(detailsBorrow.getQuantity());
        dto.setUnitPrice(detailsBorrow.getUnitPrice());
        dto.setTotalPrice(detailsBorrow.getTotalPrice());

        return dto;
    }

    @Override
    @Transactional
    public void updateBorrowStatus(Integer borrowId, Status newStatus, Integer adminId) throws Exception {
        // 1. Obtener préstamo (SRP: Obtención)
        Borrow borrow = findBorrowById(borrowId);
        
        // 2. Validar precondiciones (SRP: Validación)
        validateBorrowStatusTransition(borrow, newStatus);
        
        // 3. Resolver admin (SRP: Resolución)
        Usuario admin = findAdminById(adminId);
        borrow.setAdmin(admin);
        
        // 4. Aplicar transición de estado (SRP: State machine)
        applyStatusTransition(borrow, newStatus);
        
        // 5. Persistir (SRP: Persistencia)
        borrowRepository.save(borrow);
    }

    // ==================== HELPERS: updateBorrowStatus() ====================

    /**
     * Busca un préstamo por su ID.
     * 
     * @param borrowId ID del préstamo
     * @return Borrow entidad encontrada
     * @throws BusinessException si no existe
     */
    private Borrow findBorrowById(Integer borrowId) {
        return borrowRepository.findById(borrowId)
                .orElseThrow(() -> new BusinessException("BORROW_NOT_FOUND", 
                    "Préstamo no encontrado con ID: " + borrowId));
    }

    /**
     * Valida si la transición de estado es permitida.
     * 
     * @param borrow Préstamo actual
     * @param newStatus Nuevo estado solicitado
     * @throws BusinessException si la transición no es válida
     */
    private void validateBorrowStatusTransition(Borrow borrow, Status newStatus) {
        Status currentStatus = borrow.getStatus();
        
        if (currentStatus != Status.PENDING && currentStatus != Status.BORROWED) {
            throw new BusinessException("INVALID_STATUS_TRANSITION",
                    "Solo se puede modificar el estado de un préstamo en estado PENDING o BORROWED");
        }
        
        // Validación específica para RETURNED
        if (newStatus == Status.RETURNED && currentStatus != Status.BORROWED) {
            throw new BusinessException("INVALID_STATUS_TRANSITION",
                    "El préstamo debe estar en estado BORROWED para ser devuelto");
        }
    }

    /**
     * Busca un usuario administrador por su ID.
     * 
     * @param adminId ID del administrador
     * @return Usuario encontrado
     * @throws BusinessException si no existe
     */
    private Usuario findAdminById(Integer adminId) {
        return usuarioRepository.findById(adminId)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", 
                    "Administrador no encontrado con ID: " + adminId));
    }

    /**
     * Aplica la transición de estado al préstamo según el nuevo estado.
     * 
     * @param borrow Préstamo a transicionar
     * @param newStatus Nuevo estado
     * @throws Exception si hay error en la transición
     */
    private void applyStatusTransition(Borrow borrow, Status newStatus) throws Exception {
        switch (newStatus) {
            case REJECTED:
                handleBorrowRejected(borrow);
                break;

            case BORROWED:
                if (borrow.getStatus() == Status.PENDING) {
                    handleBorrowApproved(borrow);
                }
                break;

            case RETURNED:
                handleBorrowReturned(borrow);
                break;

            default:
                throw new BusinessException("INVALID_BORROW_STATUS", 
                    "Estado no válido para modificar el préstamo");
        }
    }

    /**
     * Maneja la transición de rechazo (PENDING → REJECTED).
     * 
     * @param borrow Préstamo a rechazar
     */
    private void handleBorrowRejected(Borrow borrow) {
        borrow.setStatus(Status.REJECTED);
        borrow.setEndDate(new Date());
    }

    /**
     * Maneja la transición de aprobación (PENDING → BORROWED).
     * Valida disponibilidad de stock y reduce el stock para cada detalle.
     * 
     * @param borrow Préstamo a aprobar
     * @throws Exception si no hay stock disponible
     */
    private void handleBorrowApproved(Borrow borrow) throws Exception {
        // Validar y reducir stock para cada detalle
        for (DetailsBorrow detail : borrow.getDetails()) {
            Integer materialId = detail.getMaterials().getId();
            int qty = detail.getQuantity();
            validateStockAvailabilityOrFallback(materialId, qty);
            reduceStockOrFallback(materialId, qty);
        }
        
        borrow.setStartDate(new Date());
        borrow.setStatus(Status.BORROWED);
        publishBorrowCreatedEvent(borrow);
    }

    /**
     * Maneja la transición de devolución (BORROWED → RETURNED).
     * Restaura el stock para cada detalle del préstamo.
     * 
     * @param borrow Préstamo a devolver
     */
    private void handleBorrowReturned(Borrow borrow) {
        // Restaurar stock para cada detalle
        for (DetailsBorrow detail : borrow.getDetails()) {
            Integer materialId = detail.getMaterials().getId();
            int qty = detail.getQuantity();
            restoreStockOrFallback(materialId, qty);
        }
        
        borrow.setStatus(Status.RETURNED);
        borrow.setReturnDate(new Date());
        borrow.setEndDate(new Date());
        publishBorrowReturnedEvent(borrow);
    }
    
    @Override
    public List<BorrowDTO> getAllBorrowDTO() {
        return borrowRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BorrowDTO> getBorrowByStatus(String status) {
        Status statusBorrow;

        switch (status.toUpperCase()) {
            case "PROCESS":
                statusBorrow = Status.PENDING;
                break;
            case "REJECTED":
                statusBorrow = Status.REJECTED;
                break;
            case "BORROWED":
            case "LOANED":
                statusBorrow = Status.BORROWED;
                break;
            case "RETURNED":
                statusBorrow = Status.RETURNED;
                break;
            default:
                throw new IllegalArgumentException("Tipo de estado inválido: " + status);
        }

        // Cambiar a List<Borrow> y luego mapear a List<BorrowDTO>
        return borrowRepository.findByStatus(statusBorrow).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BorrowDTO> getBorrowByDate(Date startDate, Date endDate) {
        //
        return borrowRepository.findByDateBetween(startDate, endDate).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Integer getUserIdFromToken(String token) {
        return TokenUtils.getUserIdFromToken(token);
    }

    // ==================== DOMAIN EVENTS ====================

    /**
     * Publica un evento cuando se crea un préstamo (transición PROCESS → BORROWED).
     * 
     * PROPÓSITO:
     * - Notificar al sistema que se ha aprobado y entregado un préstamo
     * - Permitir reacciones asíncronas (emails de confirmación, actualización de estadísticas, etc.)
     * 
     * INFORMACIÓN DEL EVENTO:
     * - ID del préstamo
     * - ID del usuario que solicitó
     * - Fechas del préstamo (fecha, inicio, fin)
     * - Monto total del préstamo
     * 
     * @param borrow El préstamo recién creado/aprobado
     */
    private void publishBorrowCreatedEvent(Borrow borrow) {
        BorrowCreatedEvent event = new BorrowCreatedEvent(borrow);
        eventPublisher.publishEvent(event);
    }

    /**
     * Publica un evento cuando se devuelve un préstamo (transición BORROWED → RETURNED).
     * 
     * PROPÓSITO:
     * - Notificar al sistema que se ha completado una devolución
     * - Detectar devoluciones tardías para aplicar penalizaciones
     * - Permitir reacciones asíncronas (emails, cálculo de multas, actualización de historial)
     * 
     * LÓGICA:
     * - El evento detecta automáticamente si la devolución fue tardía
     * - Compara returnDate con endDate para marcar wasLate=true si corresponde
     * - Los listeners pueden implementar lógica de penalización
     * 
     * @param borrow El préstamo que se devolvió
     */
    private void publishBorrowReturnedEvent(Borrow borrow) {
        Date returnDate = borrow.getReturnDate() != null ? borrow.getReturnDate() : new Date();
        BorrowReturnedEvent event = new BorrowReturnedEvent(borrow, returnDate);
        eventPublisher.publishEvent(event);
    }

}


