package com.techmate.techmate.service.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.springframework.stereotype.Service;
import java.util.*;

import com.techmate.techmate.service.MaterialsService;
import com.techmate.techmate.service.MovementsService;
import com.techmate.techmate.dto.MovementsDTO;
import com.techmate.techmate.entity.Materials;
import com.techmate.techmate.entity.MoveType;
import com.techmate.techmate.entity.Movements;
import com.techmate.techmate.entity.Usuario;
import com.techmate.techmate.repository.MaterialsRepository;
import com.techmate.techmate.repository.MovementsRepository;
import com.techmate.techmate.repository.UsuarioRepository;
import com.techmate.techmate.security.UserDetailsServiceImpl;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MovementsServiceImpl implements MovementsService {

    private static final Logger log = LoggerFactory.getLogger(MovementsServiceImpl.class);

    private final MovementsRepository movementsRepository;
    private final UsuarioRepository usuarioRepository;
    private final UserDetailsServiceImpl userService;
    private final MaterialsRepository materialsRepository;
    private final MaterialsService materialsService;
    private final com.techmate.techmate.service.movements.mapper.MovementMapper movementMapper;
    private final com.techmate.techmate.service.movements.validator.MovementValidator movementValidator;
    private final com.techmate.techmate.service.movements.manager.MovementStockManager movementStockManager;
    private final com.techmate.techmate.service.movements.query.MovementQueryService movementQueryService;

    public MovementsServiceImpl(MovementsRepository movementsRepository,
                                UsuarioRepository usuarioRepository,
                                UserDetailsServiceImpl userService,
                                MaterialsRepository materialsRepository,
                                MaterialsService materialsService,
                                com.techmate.techmate.service.movements.mapper.MovementMapper movementMapper,
                                com.techmate.techmate.service.movements.validator.MovementValidator movementValidator,
                                com.techmate.techmate.service.movements.manager.MovementStockManager movementStockManager,
                                com.techmate.techmate.service.movements.query.MovementQueryService movementQueryService) {
        this.movementsRepository = movementsRepository;
        this.usuarioRepository = usuarioRepository;
        this.userService = userService;
        this.materialsRepository = materialsRepository;
        this.materialsService = materialsService;
        this.movementMapper = movementMapper;
        this.movementValidator = movementValidator;
        this.movementStockManager = movementStockManager;
        this.movementQueryService = movementQueryService;
    }

    /**
     * ✅ SRP REFACTORING: Métodos helper para cada responsabilidad
     */

    // ─── VALIDACIÓN ───
    /**
     * Cargar y validar que el usuario exista
     */
    private Usuario loadAndValidateUser(Integer userId) {
        return usuarioRepository.findById(userId)
            .orElseThrow(() -> new com.techmate.techmate.exception.NotFoundException(
                String.format("Usuario con ID %d no encontrado", userId)
            ));
    }

    /**
     * Cargar y validar que el material exista
     */
    private Materials loadAndValidateMaterial(Integer materialId) {
        return materialsRepository.findById(materialId)
            .orElseThrow(() -> new com.techmate.techmate.exception.NotFoundException(
                String.format("Material con ID %d no encontrado", materialId)
            ));
    }

    // ─── MAPEO (DTO ↔ Entity) ───
    /**
     * Convertir DTO a Entity (con usuarios y materiales precargados)
     */
    private Movements createMovement(MovementsDTO dto, Usuario usuario, Materials materials) {
        // Delegación completa al mapper
        return movementMapper.toEntity(dto, usuario, materials);
    }

    /**
     * Convertir Entity a DTO (con nombres de usuario y material)
     */
    private MovementsDTO convertToDTO(Movements movements) {
        String adminName = userService.getUsuarioUsernamById(movements.getUsuario().getId());
        String materialName = materialsService.getMaterialsNameById(movements.getMaterials().getId());
        return movementMapper.toDTO(movements, adminName, materialName);
    }

    // ─── PREPARACIÓN ───
    /**
     * Preparar el DTO con valores por defecto
     */
    private void prepareMovementDTO(MovementsDTO dto) {
        if (dto.getDate() == null) {
            dto.setDate(new Date());
        }
        if (dto.getComment() == null) {
            dto.setComment("");
        }
    }

    // ─── STOCK ───
    /**
     * Actualizar stock del material después de persistir el movimiento
     */
    private void updateMaterialStock(Movements movement) {
        Materials materials = movement.getMaterials();
        movementStockManager.adjustMaterialStock(materials, movement);
        materialsRepository.save(materials);
    }

    @Override
    @Transactional
    public MovementsDTO createMovementsDTO(MovementsDTO movementsDTO, Integer userId) {
        // 1️⃣ VALIDAR
        movementValidator.validateQuantity(movementsDTO);
        Usuario usuario = loadAndValidateUser(userId);
        Materials materials = loadAndValidateMaterial(movementsDTO.getId());

        // 2️⃣ PREPARAR DTO
        prepareMovementDTO(movementsDTO);

        // 3️⃣ CREAR ENTIDAD
        Movements movements = createMovement(movementsDTO, usuario, materials);

        // 4️⃣ PERSISTIR
        movements = movementsRepository.save(movements);

        // 5️⃣ AJUSTAR STOCK
        updateMaterialStock(movements);

        // 6️⃣ RETORNAR DTO
        return convertToDTO(movements);
    }

    // Ajustar stock de material
    // Stock adjustments now delegated to MovementStockManager

    @Override
    @Transactional
    public MovementsDTO getMovementsByID(Integer movementsId) {
        // 1️⃣ CARGAR movimiento
        Movements movements = movementsRepository.findById(movementsId)
            .orElseThrow(() -> new com.techmate.techmate.exception.NotFoundException(
                String.format("Movimiento con ID %d no encontrado", movementsId)
            ));

        // 2️⃣ CONVERTIR a DTO
        return convertToDTO(movements);
    }

    @Override
    public List<MovementsDTO> getAllMovementsDTO() {
        return movementQueryService.getAll();
    }

    @Override
    @Transactional
    public MovementsDTO updateMovement(Integer movementsId, MovementsDTO movementsDTO) {
        // 1️⃣ CARGAR movimiento existente
        Movements movement = movementsRepository.findById(movementsId)
            .orElseThrow(() -> new com.techmate.techmate.exception.NotFoundException(
                String.format("Movimiento con ID %d no encontrado", movementsId)
            ));

        // 2️⃣ VALIDAR nuevos datos
        movementValidator.validateQuantity(movementsDTO);

        // 3️⃣ ACTUALIZAR campos
        if (movementsDTO.getComment() != null) {
            movement.setComment(movementsDTO.getComment());
        }

        // 4️⃣ PERSISTIR
        movement = movementsRepository.save(movement);

        // 5️⃣ RETORNAR DTO
        return convertToDTO(movement);
    }

    @Override
    public List<MovementsDTO> getMovementsByType(String type) {
        // ✅ TAREA 3 (OCP) será: Centralizar en MoveTypeConverter
        // Por ahora, mejorar legibilidad
        MoveType moveType = parseMoveType(type);
        return movementQueryService.getByMoveType(moveType);
    }

    /**
     * Convertir string a MoveType enum
     * (Este método será reemplazado por MoveTypeConverter en TAREA 3)
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
                    String.format("Tipo de movimiento inválido: %s. Tipos válidos: STOCK_ADD, RETURN, BORROW, ADJUSTMENT", type)
                );
        }
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

        // 2️⃣ OBTENER todos y aplicar paginación manual
        List<MovementsDTO> allMovements = movementQueryService.getAll();
        
        // 3️⃣ CALCULAR índices de paginación
        int startIndex = pageNumber * pageSize;
        int endIndex = Math.min(startIndex + pageSize, allMovements.size());
        
        // 4️⃣ RETORNAR sublist paginada
        if (startIndex >= allMovements.size()) {
            return Collections.emptyList();
        }
        
        return allMovements.subList(startIndex, endIndex);
    }

    @Override
    @Transactional
    public void deleteMovementById(Integer movementsId) {
        // 1️⃣ VERIFICAR que existe
        movementsRepository.findById(movementsId)
            .orElseThrow(() -> new EntityNotFoundException(
                String.format("Movimiento con ID %d no encontrado", movementsId)
            ));

        // 2️⃣ ELIMINAR
        movementsRepository.deleteById(movementsId);

        log.info("Movimiento con ID {} eliminado correctamente", movementsId);
    }

}


