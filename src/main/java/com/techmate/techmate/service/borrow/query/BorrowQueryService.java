package com.techmate.techmate.service.borrow.query;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.techmate.techmate.dto.BorrowDTO;
import com.techmate.techmate.entity.Status;
import com.techmate.techmate.repository.BorrowRepository;
import com.techmate.techmate.service.borrow.mapper.BorrowMapper;

/**
 * 🎯 Servicio de consultas especializado para préstamos siguiendo SRP.
 * 
 * PRINCIPIOS SOLID APLICADOS:
 * - SRP: Solo se encarga de consultas y filtros de préstamos
 * - OCP: Extensible para nuevos tipos de consultas sin modificar código existente
 * - LSP: Puede ser sustituido por cualquier implementación del contrato
 * - ISP: Interfaz específica para consultas de préstamos
 * - DIP: Depende de abstracciones (Repository, Mapper) no de implementaciones
 * 
 * RESPONSABILIDADES:
 * - Obtener todos los préstamos
 * - Filtrar préstamos por estado
 * - Filtrar préstamos por rango de fechas
 * - Buscar préstamos por usuario
 * - Convertir entidades a DTOs usando el mapper
 * 
 * @author TechShare Team - SOLID Implementation
 */
@Component
public class BorrowQueryService {
    
    private final BorrowRepository borrowRepository;
    private final BorrowMapper borrowMapper;
    
    /**
     * Constructor injection para cumplir con DIP.
     */
    public BorrowQueryService(BorrowRepository borrowRepository, BorrowMapper borrowMapper) {
        this.borrowRepository = borrowRepository;
        this.borrowMapper = borrowMapper;
    }
    
    /**
     * Obtiene todos los préstamos convertidos a DTO.
     * 
     * @return Lista de préstamos como DTO
     */
    @Transactional(readOnly = true)
    public List<BorrowDTO> getAllBorrows() {
        return borrowRepository.findAll().stream()
                .map(borrowMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene préstamos filtrados por estado con parsing inteligente.
     * 
     * @param statusString Estado como string (case-insensitive)
     * @return Lista de préstamos filtrados
     * @throws IllegalArgumentException si el estado no es válido
     */
    @Transactional(readOnly = true)
    public List<BorrowDTO> getBorrowsByStatus(String statusString) {
        Status status = parseStatus(statusString);
        
        return borrowRepository.findByStatus(status).stream()
                .map(borrowMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene préstamos filtrados por rango de fechas.
     * 
     * @param startDate Fecha de inicio (inclusive)
     * @param endDate Fecha de fin (inclusive)
     * @return Lista de préstamos en el rango de fechas
     */
    @Transactional(readOnly = true)
    public List<BorrowDTO> getBorrowsByDateRange(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Las fechas de inicio y fin son requeridas");
        }
        
        if (startDate.after(endDate)) {
            throw new IllegalArgumentException("La fecha de inicio debe ser anterior a la fecha de fin");
        }
        
        return borrowRepository.findByDateBetween(startDate, endDate).stream()
                .map(borrowMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene préstamos de un usuario específico.
     * 
     * @param userId ID del usuario
     * @return Lista de préstamos del usuario
     */
    @Transactional(readOnly = true)
    public List<BorrowDTO> getBorrowsByUser(Integer userId) {
        if (userId == null) {
            throw new IllegalArgumentException("El ID del usuario es requerido");
        }
        
        return borrowRepository.findByUsuarioId(userId).stream()
                .map(borrowMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene un préstamo específico por ID.
     * 
     * @param borrowId ID del préstamo
     * @return DTO del préstamo
     * @throws RuntimeException si el préstamo no existe
     */
    @Transactional(readOnly = true)
    public BorrowDTO getBorrowById(Integer borrowId) {
        if (borrowId == null) {
            throw new IllegalArgumentException("El ID del préstamo es requerido");
        }
        
        return borrowRepository.findById(borrowId)
                .map(borrowMapper::toDTO)
                .orElseThrow(() -> new RuntimeException(
                    String.format("Préstamo no encontrado con ID: %d", borrowId)));
    }
    
    /**
     * Obtiene préstamos activos (PROCESS y BORROWED).
     * 
     * @return Lista de préstamos activos
     */
    public List<BorrowDTO> getActiveBorrows() {
    List<BorrowDTO> pendingBorrows = getBorrowsByStatus("PENDING");
    List<BorrowDTO> borrowedBorrows = getBorrowsByStatus("BORROWED");

    pendingBorrows.addAll(borrowedBorrows);
    return pendingBorrows;
    }
    
    // ==================== MÉTODOS PRIVADOS ====================
    
    /**
     * Parsea un string a enum Status de forma inteligente.
     * Soporta case-insensitive y variaciones comunes.
     * 
     * @param statusString String del estado
     * @return Enum Status correspondiente
     * @throws IllegalArgumentException si el estado no es válido
     */
    private Status parseStatus(String statusString) {
        if (statusString == null || statusString.trim().isEmpty()) {
            throw new IllegalArgumentException("El estado es requerido");
        }
        
        String normalizedStatus = statusString.trim().toUpperCase();
        
        switch (normalizedStatus) {
            case "PROCESS":
            case "PROCESSING":
            case "PENDING":
                return Status.PENDING;

            case "REJECTED":
            case "DENIED":
            case "CANCELLED":
                return Status.REJECTED;

            case "BORROWED":
            case "APPROVED":
            case "ACTIVE":
            case "LOANED":
                return Status.BORROWED;

            case "RETURNED":
            case "COMPLETED":
            case "FINISHED":
                return Status.RETURNED;

            default:
                throw new IllegalArgumentException(
                    String.format("Estado inválido: %s. Estados válidos: PENDING, REJECTED, BORROWED, RETURNED", 
                        statusString));
        }
    }
}

