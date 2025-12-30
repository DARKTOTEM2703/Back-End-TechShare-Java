package com.techmate.techmate.hexagonal.infrastructure.service.movements.query;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.techmate.techmate.hexagonal.infrastructure.dto.MovementsDTO;
import com.techmate.techmate.hexagonal.domain.entity.MoveType;
import com.techmate.techmate.hexagonal.domain.entity.Movements;
import com.techmate.techmate.hexagonal.domain.repository.MovementsRepository;
import com.techmate.techmate.hexagonal.infrastructure.service.movements.mapper.MovementMapper;

@Component
@Transactional(readOnly = true)
public class MovementQueryService {

    private final MovementsRepository movementsRepository;
    private final MovementMapper movementMapper;

    public MovementQueryService(MovementsRepository movementsRepository, MovementMapper movementMapper) {
        this.movementsRepository = movementsRepository;
        this.movementMapper = movementMapper;
    }

    public List<MovementsDTO> getAll() {
        return movementsRepository.findAll().stream()
                .map(m -> movementMapper.toDTO(m, null, null))
                .collect(Collectors.toList());
    }

    public MovementsDTO getById(Integer id) {
        return movementsRepository.findById(id)
                .map(m -> movementMapper.toDTO(m, null, null))
                .orElse(null);
    }

    public List<MovementsDTO> getByMoveType(MoveType moveType) {
        return movementsRepository.findByMoveType(moveType).stream()
                .map(m -> movementMapper.toDTO(m, null, null))
                .collect(Collectors.toList());
    }

    public List<MovementsDTO> getByDateRange(Date start, Date end) {
        return movementsRepository.findByMovementDateBetween(start, end).stream()
                .map(m -> movementMapper.toDTO(m, null, null))
                .collect(Collectors.toList());
    }

    public List<MovementsDTO> getPaged(Pageable pageable) {
        Page<Movements> movementsPage = movementsRepository.findAllOptimizedPaginated(pageable);
        return movementsPage.getContent().stream()
                .map(m -> movementMapper.toDTO(m, null, null))
                .collect(Collectors.toList());
    }
}






