package com.techmate.techmate.hexagonal.infrastructure.service.movements.manager;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.techmate.techmate.hexagonal.infrastructure.dto.MovementsDTO;
import com.techmate.techmate.hexagonal.domain.entity.Materials;
import com.techmate.techmate.hexagonal.domain.entity.Movements;
import com.techmate.techmate.hexagonal.domain.entity.Usuario;
import com.techmate.techmate.hexagonal.domain.repository.MaterialsRepository;
import com.techmate.techmate.hexagonal.domain.repository.MovementsRepository;
import com.techmate.techmate.hexagonal.domain.repository.UsuarioRepository;
import com.techmate.techmate.hexagonal.infrastructure.service.MaterialsService;
import com.techmate.techmate.hexagonal.infrastructure.service.movements.mapper.MovementMapper;
import com.techmate.techmate.hexagonal.infrastructure.service.movements.validator.MovementValidator;
import com.techmate.techmate.hexagonal.infrastructure.security.UserDetailsServiceImpl;

import jakarta.persistence.EntityNotFoundException;

@Component
public class MovementManager {

    private static final Logger log = LoggerFactory.getLogger(MovementManager.class);

    private final MovementsRepository movementsRepository;
    private final UsuarioRepository usuarioRepository;
    private final MaterialsRepository materialsRepository;
    private final UserDetailsServiceImpl userService;
    private final MaterialsService materialsService;
    private final MovementMapper movementMapper;
    private final MovementValidator movementValidator;
    private final MovementStockManager movementStockManager;

    public MovementManager(
            MovementsRepository movementsRepository,
            UsuarioRepository usuarioRepository,
            MaterialsRepository materialsRepository,
            UserDetailsServiceImpl userService,
            MaterialsService materialsService,
            MovementMapper movementMapper,
            MovementValidator movementValidator,
            MovementStockManager movementStockManager) {
        this.movementsRepository = movementsRepository;
        this.usuarioRepository = usuarioRepository;
        this.materialsRepository = materialsRepository;
        this.userService = userService;
        this.materialsService = materialsService;
        this.movementMapper = movementMapper;
        this.movementValidator = movementValidator;
        this.movementStockManager = movementStockManager;
    }

    /**
     * Cargar y validar que el usuario exista
     */
    private Usuario loadAndValidateUser(Integer userId) {
        return usuarioRepository.findById(userId)
                .orElseThrow(() -> new com.techmate.techmate.hexagonal.infrastructure.exception.NotFoundException(
                        String.format("Usuario con ID %d no encontrado", userId)));
    }

    /**
     * Cargar y validar que el material exista
     */
    private Materials loadAndValidateMaterial(Integer materialId) {
        return materialsRepository.findById(materialId)
                .orElseThrow(() -> new com.techmate.techmate.hexagonal.infrastructure.exception.NotFoundException(
                        String.format("Material con ID %d no encontrado", materialId)));
    }

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

    /**
     * Actualizar stock del material después de persistir el movimiento
     */
    private void updateMaterialStock(Movements movement) {
        Materials materials = movement.getMaterials();
        movementStockManager.adjustMaterialStock(materials, movement);
        materialsRepository.save(materials);
    }

    @Transactional
    public MovementsDTO createMovement(MovementsDTO movementsDTO, Integer userId) {
        // 1️⃣ VALIDAR
        movementValidator.validateQuantity(movementsDTO);
        Usuario usuario = loadAndValidateUser(userId);
        Materials materials = loadAndValidateMaterial(movementsDTO.getId());

        // 2️⃣ PREPARAR DTO
        prepareMovementDTO(movementsDTO);

        // 3️⃣ CREAR ENTIDAD
        Movements movements = movementMapper.toEntity(movementsDTO, usuario, materials);

        // 4️⃣ PERSISTIR
        movements = movementsRepository.save(movements);

        // 5️⃣ AJUSTAR STOCK
        updateMaterialStock(movements);

        // 6️⃣ RETORNAR DTO
        String adminName = userService.getUsuarioUsernamById(movements.getUsuario().getId());
        String materialName = materialsService.getMaterialsNameById(movements.getMaterials().getId());
        return movementMapper.toDTO(movements, adminName, materialName);
    }

    @Transactional
    public MovementsDTO updateMovement(Integer movementsId, MovementsDTO movementsDTO) {
        // 1️⃣ CARGAR movimiento existente
        Movements movement = movementsRepository.findById(movementsId)
                .orElseThrow(() -> new com.techmate.techmate.hexagonal.infrastructure.exception.NotFoundException(
                        String.format("Movimiento con ID %d no encontrado", movementsId)));

        // 2️⃣ VALIDAR nuevos datos
        movementValidator.validateQuantity(movementsDTO);

        // 3️⃣ ACTUALIZAR campos
        if (movementsDTO.getComment() != null) {
            movement.setComment(movementsDTO.getComment());
        }

        // 4️⃣ PERSISTIR
        movement = movementsRepository.save(movement);

        // 5️⃣ RETORNAR DTO
        String adminName = userService.getUsuarioUsernamById(movement.getUsuario().getId());
        String materialName = materialsService.getMaterialsNameById(movement.getMaterials().getId());
        return movementMapper.toDTO(movement, adminName, materialName);
    }

    @Transactional
    public void deleteMovement(Integer movementsId) {
        // 1️⃣ VERIFICAR que existe
        movementsRepository.findById(movementsId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Movimiento con ID %d no encontrado", movementsId)));

        // 2️⃣ ELIMINAR
        movementsRepository.deleteById(movementsId);

        log.info("Movimiento con ID {} eliminado correctamente", movementsId);
    }
}







