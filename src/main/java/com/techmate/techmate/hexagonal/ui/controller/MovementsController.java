package com.techmate.techmate.hexagonal.ui.controller;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.techmate.techmate.hexagonal.infrastructure.dto.MovementsDTO;
import com.techmate.techmate.hexagonal.infrastructure.dto.MovementResponse;
import com.techmate.techmate.hexagonal.domain.entity.MoveType;
import com.techmate.techmate.hexagonal.infrastructure.security.TokenUtils;
import com.techmate.techmate.hexagonal.infrastructure.service.MovementsService;
import com.techmate.techmate.hexagonal.infrastructure.service.movements.mapper.MovementsMapper;

// CORS configurado globalmente en WebSecurityConfig - no necesita @CrossOrigin aquí
@RestController
@RequestMapping("/admin/movement")
public class MovementsController {

    private static final Logger log = LoggerFactory.getLogger(MovementsController.class);

    private final MovementsService movementsService;
    private final MovementsMapper movementsMapper;

    // Crear un nuevo movimiento
    public MovementsController(MovementsService movementsService, MovementsMapper movementsMapper) {
        this.movementsService = movementsService;
        this.movementsMapper = movementsMapper;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createMovement(
            @RequestParam("quantity") @jakarta.validation.constraints.NotNull(message = "La cantidad no puede ser nula") @jakarta.validation.constraints.Min(value = 1, message = "La cantidad debe ser mayor a 0") Integer quantity,

            @RequestParam("moveType") @jakarta.validation.constraints.NotNull(message = "El tipo de movimiento no puede ser nulo") MoveType moveType,

            @RequestParam("id_material") @jakarta.validation.constraints.NotNull(message = "El ID del material no puede ser nulo") @jakarta.validation.constraints.Min(value = 1, message = "El ID del material debe ser mayor a 0") Integer idMaterial,

            @RequestParam(value = "comment", required = false) @jakarta.validation.constraints.Size(max = 500, message = "El comentario no puede exceder 500 caracteres") @com.techmate.techmate.validation.SafeString(allowSpecial = true) String comment, // Agregar
                                                                                                                                                                                                                                                            // comentario
                                                                                                                                                                                                                                                            // opcional

            Authentication authentication) {

        MovementsDTO movementsDTO = new MovementsDTO();
        movementsDTO.setQuantity(quantity);
        movementsDTO.setMoveType(moveType);
        movementsDTO.setId(idMaterial);
        movementsDTO.setDate(new Date());
        movementsDTO.setComment(comment); // Establecer el comentario

        Integer userId = null;

        // Obtener información del usuario desde el SecurityContext
        String userEmail = authentication.getName(); // Obtenemos el email del usuario autenticado
        log.info("Usuario autenticado: {}", userEmail);

        // Obtener roles del usuario
        String userRole = TokenUtils.getAuthenticatedUserRole();
        log.info("Rol del usuario: {}", userRole);

        // TODO: Implementar método en el servicio para obtener userId por email
        // Por ahora usamos un placeholder - en una implementación real:
        // userId = userService.getUserIdByEmail(userEmail);
        userId = extractUserIdFromAuthentication(authentication);

        MovementsDTO createdMovement = movementsService.createMovementsDTO(movementsDTO, userId);
        MovementResponse resp = movementsMapper.toResponse(createdMovement);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    // Obtener un movimiento por ID
    @GetMapping("/{id}")
    public ResponseEntity<MovementResponse> getMovementById(@PathVariable("id") Integer id) {

        MovementsDTO movementsDTO = movementsService.getMovementsByID(id);
        if (movementsDTO == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        MovementResponse resp = movementsMapper.toResponse(movementsDTO);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/all")
    public ResponseEntity<List<MovementResponse>> getAllMovements() {

        // Llamar al servicio para obtener la lista de movimientos
        List<MovementsDTO> movementsList = movementsService.getAllMovementsDTO();

        // Verificar si la lista está vacía
        if (movementsList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        // Mapear a response público y devolver la lista
        List<MovementResponse> resp = movementsList.stream()
                .map(movementsMapper::toResponse)
                .toList();

        // Devolver la lista de movimientos con el código HTTP 200 OK
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<MovementResponse>> getMovementsByType(@PathVariable String type) {

        List<MovementsDTO> movementDTOsList = movementsService.getMovementsByType(type);

        if (movementDTOsList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        List<MovementResponse> resp = movementDTOsList.stream()
                .map(movementsMapper::toResponse)
                .toList();

        return ResponseEntity.ok(resp);
    }

    @GetMapping("/filterByDate")
    public ResponseEntity<List<MovementResponse>> getMovementsByDate(
            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {

        List<MovementsDTO> movementsDTOs = movementsService.getMovementsByDate(startDate, endDate);

        if (movementsDTOs.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        List<MovementResponse> resp = movementsDTOs.stream()
                .map(movementsMapper::toResponse)
                .toList();

        return ResponseEntity.ok(resp);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteMovement(@PathVariable Integer id) {

        movementsService.deleteMovementById(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    /**
     * Método helper para extraer userId del Authentication.
     * 
     * @throws AccessDeniedException si no se puede extraer el userId
     */
    private Integer extractUserIdFromAuthentication(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Usuario no autenticado. No se puede procesar el movimiento.");
        }

        try {
            // Intentar extraer de diferentes formas según el tipo de Authentication
            if (authentication.getPrincipal() instanceof String) {
                // El username/email está en principal
                String userEmail = (String) authentication.getPrincipal();
                log.info("Usuario autenticado por email: {}", userEmail);
            }

            // Intentar obtener el ID desde los detalles del token JWT
            if (authentication.getDetails() instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
                if (details.containsKey("id")) {
                    return ((Number) details.get("id")).intValue();
                }
            }

            // Intentar extraer del token JWT en credentials
            if (authentication.getCredentials() != null) {
                String token = authentication.getCredentials().toString();
                Integer userId = TokenUtils.getUserIdFromToken(token);
                if (userId != null && userId > 0) {
                    return userId;
                }
            }

            // Si no se puede extraer de ninguna forma, lanzar excepción
            log.error("No se pudo extraer userId del Authentication");
            throw new org.springframework.security.access.AccessDeniedException(
                    "No se pudo extraer el userId del token de autenticación.");
        } catch (org.springframework.security.access.AccessDeniedException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al extraer userId: {}", e.getMessage());
            throw new org.springframework.security.access.AccessDeniedException(
                    "Error al procesar la autenticación: " + e.getMessage());
        }
    }

}


