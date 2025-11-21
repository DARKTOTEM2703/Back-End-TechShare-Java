package com.techmate.techmate.service;

import com.techmate.techmate.dto.MovementsDTO;

/**
 * ✅ Interface Segregation Principle (ISP)
 * 
 * Interfaz segregada para operaciones CRUD de movimientos.
 * Responsabilidad ÚNICA: Crear, Leer, Actualizar, Eliminar movimientos
 * 
 * ✨ Beneficio: Los clientes que solo necesitan CRUD no son obligados
 * a implementar o mockear métodos de queries o token handling
 * 
 * @author TechShare Team
 * @version 1.0
 */
public interface IMovementCrudService {

    /**
     * Crear un nuevo movimiento
     * @param movementsDTO datos del movimiento
     * @param userId ID del usuario que crea el movimiento
     * @return movimiento creado
     */
    MovementsDTO createMovementsDTO(MovementsDTO movementsDTO, Integer userId);

    /**
     * Obtener movimiento por ID
     * @param movementsId ID del movimiento
     * @return datos del movimiento
     */
    MovementsDTO getMovementsByID(Integer movementsId);

    /**
     * Actualizar un movimiento existente
     * @param movementsId ID del movimiento a actualizar
     * @param movementsDTO nuevos datos
     * @return movimiento actualizado
     */
    MovementsDTO updateMovement(Integer movementsId, MovementsDTO movementsDTO);

    /**
     * Eliminar un movimiento por ID
     * @param movementsId ID del movimiento a eliminar
     */
    void deleteMovementById(Integer movementsId);

}

