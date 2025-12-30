package com.techmate.techmate.hexagonal.infrastructure.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import java.util.*;

import com.techmate.techmate.hexagonal.infrastructure.service.MovementsService;
import com.techmate.techmate.hexagonal.infrastructure.dto.MovementsDTO;
import com.techmate.techmate.hexagonal.domain.entity.MoveType;
import com.techmate.techmate.hexagonal.infrastructure.service.movements.mapper.MovementMapper;
import com.techmate.techmate.hexagonal.infrastructure.service.movements.manager.MovementManager;
import com.techmate.techmate.hexagonal.infrastructure.service.movements.query.MovementQueryService;

import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MovementsServiceImpl implements MovementsService {

    private static final Logger log = LoggerFactory.getLogger(MovementsServiceImpl.class);

    private final MovementManager movementManager;
    private final MovementQueryService movementQueryService;
    private final MovementMapper movementMapper;

    public MovementsServiceImpl(
            MovementManager movementManager,
            MovementQueryService movementQueryService,
            MovementMapper movementMapper) {
        this.movementManager = movementManager;
        this.movementQueryService = movementQueryService;
        this.movementMapper = movementMapper;
    }

    /**
     * Convertir string a MoveType enum
     * TODO: En futuras refactorizaciones, extraer a MoveTypeConverter (OCP)
     */
    private MoveType parseMoveType(String type) {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Tipo de movimiento no puede estar vacío");
        }

        switch (type.toUpperCase()) {
            case "STOCK_ADD":
            case "IN":
                return MoveType.STOCK_ADD;
            case "RETURN":
            case "OUT":
                return MoveType.RETURN;
            case "BORROW":
                return MoveType.BORROW;
            case "ADJUSTMENT":
            case "ADJUST":
                return MoveType.ADJUSTMENT;
            default:
                throw new IllegalArgumentException(
                        String.format(
                                "Tipo de movimiento inválido: %s. Tipos válidos: STOCK_ADD, RETURN, BORROW, ADJUSTMENT",
                                type));
        }
    }

    @Override
    @Transactional
    public MovementsDTO createMovementsDTO(MovementsDTO movementsDTO, Integer userId) {
        return movementManager.createMovement(movementsDTO, userId);
    }

    @Override
    @Transactional
    public MovementsDTO getMovementsByID(Integer movementsId) {
        return movementQueryService.getById(movementsId);
    }

    @Override
    public List<MovementsDTO> getAllMovementsDTO() {
        return movementQueryService.getAll();
    }

    @Override
    @Transactional
    public MovementsDTO updateMovement(Integer movementsId, MovementsDTO movementsDTO) {
        return movementManager.updateMovement(movementsId, movementsDTO);
    }

    @Override
    public List<MovementsDTO> getMovementsByType(String type) {
        MoveType moveType = parseMoveType(type);
        return movementQueryService.getByMoveType(moveType);
    }

    @Override
    public List<MovementsDTO> getMovementsByDate(Date startDate, Date endDate) {
        // 1️⃣ VALIDAR fechas
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Las fechas inicial y final son requeridas");
        }
        if (startDate.after(endDate)) {
            throw new IllegalArgumentException("La fecha inicial debe ser anterior a la fecha final");
        }

        // 2️⃣ DELEGAR a query service
        return movementQueryService.getByDateRange(startDate, endDate);
    }

    @Override
    public List<MovementsDTO> getMovementsPaged(Integer pageNumber, Integer pageSize) {
        // 1️⃣ VALIDAR parámetros
        if (pageNumber == null || pageNumber < 0) {
            throw new IllegalArgumentException("El número de página debe ser >= 0");
        }
        if (pageSize == null || pageSize <= 0) {
            throw new IllegalArgumentException("El tamaño de página debe ser > 0");
        }

        // 2️⃣ CREAR Pageable para paginación a nivel de BD
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);

        // 3️⃣ DELEGAR a query service (obtiene página desde BD, NO carga toda en
        // memoria)
        return movementQueryService.getPaged(pageRequest);
    }

    @Override
    @Transactional
    public void deleteMovementById(Integer movementsId) {
        movementManager.deleteMovement(movementsId);
    }

}







