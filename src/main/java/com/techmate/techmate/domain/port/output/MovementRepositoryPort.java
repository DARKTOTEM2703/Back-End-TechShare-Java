package com.techmate.techmate.domain.port.output;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.techmate.techmate.domain.model.movement.Movement;
import com.techmate.techmate.domain.model.movement.MoveType;

/**
 * MovementRepositoryPort - Output port for movement persistence operations.
 * Defines contracts for CRUD and query operations on movements.
 * 
 * Implementation must be provided by infrastructure layer (JPA adapters).
 * Zero Spring dependencies - pure business interface.
 * 
 * @author TechShare Team
 * @version 1.0
 */
public interface MovementRepositoryPort {

    /**
     * Find movement by ID
     * @param id movement identifier
     * @return Optional containing movement or empty if not found
     */
    Optional<Movement> findById(Integer id);

    /**
     * Find all movements
     * @return list of all movements
     */
    List<Movement> findAll();

    /**
     * Find movements by type
     * @param moveType the type of movement
     * @return list of movements of specified type
     */
    List<Movement> findByMoveType(MoveType moveType);

    /**
     * Find movements within date range
     * @param startDate start of range (inclusive)
     * @param endDate end of range (inclusive)
     * @return list of movements within date range
     */
    List<Movement> findByDateRange(Date startDate, Date endDate);

    /**
     * Find movements by user ID
     * @param userId the user identifier
     * @return list of movements created by user
     */
    List<Movement> findByUserId(Integer userId);

    /**
     * Find movements by material ID
     * @param materialId the material identifier
     * @return list of movements for material
     */
    List<Movement> findByMaterialId(Integer materialId);

    /**
     * Check if movement exists by ID
     * @param id movement identifier
     * @return true if exists, false otherwise
     */
    boolean existsById(Integer id);

    /**
     * Count total movements
     * @return total count of movements
     */
    long count();

    /**
     * Save (create or update) a movement
     * @param movement the movement to persist
     * @return the persisted movement with assigned ID
     */
    Movement save(Movement movement);

    /**
     * Delete movement by ID
     * @param id movement identifier
     */
    void deleteById(Integer id);

    /**
     * Find movements paged
     * @param pageNumber page number (starting from 0)
     * @param pageSize page size
     * @return list of movements for the page
     */
    List<Movement> findPaged(Integer pageNumber, Integer pageSize);
}
