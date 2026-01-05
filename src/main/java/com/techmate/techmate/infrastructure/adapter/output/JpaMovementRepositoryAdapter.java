package com.techmate.techmate.infrastructure.adapter.output;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.techmate.techmate.infrastructure.persistence.entity.Movements;
import com.techmate.techmate.infrastructure.persistence.entity.Materials;
import com.techmate.techmate.infrastructure.persistence.entity.Usuario;
import com.techmate.techmate.infrastructure.persistence.repository.MovementsRepository;
import com.techmate.techmate.infrastructure.persistence.repository.MaterialsRepository;
import com.techmate.techmate.infrastructure.persistence.repository.UsuarioRepository;
import com.techmate.techmate.core.domain.model.movement.Movement;
import com.techmate.techmate.core.domain.model.movement.MoveType;
import com.techmate.techmate.domain.port.output.MovementRepositoryPort;
import com.techmate.techmate.core.application.mapper.DomainMovementMapper;

/**
 * JpaMovementRepositoryAdapter - Infrastructure adapter implementing
 * MovementRepositoryPort.
 * Bridges between domain (pure business logic) and JPA persistence layer.
 * 
 * Responsibilities:
 * - Translate domain Movement ↔ JPA Movements entity
 * - Execute JPA queries and return domain models
 * - Manage transaction boundaries
 * 
 * @author TechShare Team
 * @version 1.0
 */
@Repository
public class JpaMovementRepositoryAdapter implements MovementRepositoryPort {

    private final MovementsRepository movementsRepository;
    private final MaterialsRepository materialsRepository;
    private final UsuarioRepository usuarioRepository;
    private final DomainMovementMapper movementMapper;

    public JpaMovementRepositoryAdapter(
            MovementsRepository movementsRepository,
            MaterialsRepository materialsRepository,
            UsuarioRepository usuarioRepository,
            DomainMovementMapper movementMapper) {
        this.movementsRepository = movementsRepository;
        this.materialsRepository = materialsRepository;
        this.usuarioRepository = usuarioRepository;
        this.movementMapper = movementMapper;
    }

    @Override
    public Optional<Movement> findById(Integer id) {
        return movementsRepository.findById(id)
                .map(movementMapper::toDomain);
    }

    @Override
    public List<Movement> findAll() {
        return movementsRepository.findAll().stream()
                .map(movementMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Movement> findByMoveType(MoveType moveType) {
        com.techmate.techmate.infrastructure.persistence.entity.MoveType jpaMoveType = convertMoveType(moveType);
        return movementsRepository.findByMoveType(jpaMoveType).stream()
                .map(movementMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Movement> findByDateRange(Date startDate, Date endDate) {
        return movementsRepository.findAll().stream()
                .filter(m -> m.getMovementDate() != null &&
                        !m.getMovementDate().before(startDate) &&
                        !m.getMovementDate().after(endDate))
                .map(movementMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Movement> findByUserId(Integer userId) {
        return movementsRepository.findAll().stream()
                .filter(m -> m.getUsuario() != null && m.getUsuario().getId() == userId)
                .map(movementMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Movement> findByMaterialId(Integer materialId) {
        return movementsRepository.findAll().stream()
                .filter(m -> m.getMaterials() != null && m.getMaterials().getId() == materialId)
                .map(movementMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(Integer id) {
        return movementsRepository.existsById(id);
    }

    @Override
    public long count() {
        return movementsRepository.count();
    }

    @Override
    public Movement save(Movement movement) {
        if (movement == null) {
            throw new IllegalArgumentException("Movement cannot be null");
        }

        // Fetch related entities
        Usuario usuario = usuarioRepository.findById(movement.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario not found with ID: " + movement.getUserId()));

        Materials materials = materialsRepository.findById(movement.getMaterialId())
                .orElseThrow(() -> new RuntimeException("Material not found with ID: " + movement.getMaterialId()));

        // Convert domain to JPA entity
        Movements jpaMovement = movementMapper.toJpaEntity(movement, usuario, materials);

        // Persist and return domain model
        Movements savedJpaMovement = movementsRepository.save(jpaMovement);
        return movementMapper.toDomain(savedJpaMovement);
    }

    @Override
    public void deleteById(Integer id) {
        if (!movementsRepository.existsById(id)) {
            throw new RuntimeException("Movement not found with ID: " + id);
        }
        movementsRepository.deleteById(id);
    }

    @Override
    public List<Movement> findPaged(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return movementsRepository.findAll(pageable).stream()
                .map(movementMapper::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * Convert domain MoveType to JPA MoveType enum
     */
    private com.techmate.techmate.infrastructure.persistence.entity.MoveType convertMoveType(MoveType domainType) {
        if (domainType == null) {
            return null;
        }

        switch (domainType) {
            case BORROW:
                return com.techmate.techmate.infrastructure.persistence.entity.MoveType.BORROW;
            case RETURN:
                return com.techmate.techmate.infrastructure.persistence.entity.MoveType.RETURN;
            case STOCK_ADD:
                return com.techmate.techmate.infrastructure.persistence.entity.MoveType.STOCK_ADD;
            case ADJUSTMENT:
                return com.techmate.techmate.infrastructure.persistence.entity.MoveType.ADJUSTMENT;
            default:
                return null;
        }
    }
}
