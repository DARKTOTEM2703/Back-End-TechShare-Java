package com.techmate.techmate.application.usecase;

import com.techmate.techmate.domain.model.Borrow;
import com.techmate.techmate.domain.port.out.BorrowRepositoryPort;
import com.techmate.techmate.domain.port.out.UsuarioRepositoryPort;
import com.techmate.techmate.domain.port.out.MaterialsRepositoryPort;
import com.techmate.techmate.dto.BorrowReadDTO;
import com.techmate.techmate.service.borrow.mapper.BorrowDtoMapper;
import com.techmate.techmate.entity.Usuario;
import com.techmate.techmate.domain.model.Material;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class GetBorrowsUseCase {

    private final BorrowRepositoryPort borrowRepositoryPort;
    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final MaterialsRepositoryPort materialsRepositoryPort;
    private final BorrowDtoMapper borrowDtoMapper;

    public GetBorrowsUseCase(BorrowRepositoryPort borrowRepositoryPort,
            UsuarioRepositoryPort usuarioRepositoryPort,
            MaterialsRepositoryPort materialsRepositoryPort,
            BorrowDtoMapper borrowDtoMapper) {
        this.borrowRepositoryPort = borrowRepositoryPort;
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.materialsRepositoryPort = materialsRepositoryPort;
        this.borrowDtoMapper = borrowDtoMapper;
    }

    @Transactional(readOnly = true)
    public List<BorrowReadDTO> getAll() {
        List<Borrow> borrows = borrowRepositoryPort.findAll();
        // Collect user ids
        List<Integer> userIds = borrows.stream()
                .map(Borrow::getUsuarioId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        // Collect material ids referenced directly in the borrow (resourceId)
        Set<Integer> materialIdsFromBorrow = borrows.stream()
                .map(Borrow::getResourceId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // Collect material ids referenced by details to avoid N+1
        Set<Integer> materialIdsFromDetails = borrows.stream()
                .flatMap(b -> b.getDetails() == null ? Stream.empty() : b.getDetails().stream())
                .map(d -> d.getMaterialsId())
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // Combine both sets
        Set<Integer> combinedMaterialIds = Stream
                .concat(materialIdsFromBorrow.stream(), materialIdsFromDetails.stream())
                .collect(Collectors.toSet());

        Map<Integer, Usuario> usersById = usuarioRepositoryPort.findAllByIds(userIds == null ? List.of() : userIds)
                .stream()
                .collect(Collectors.toMap(Usuario::getId, u -> u));

        Map<Integer, Material> materialsById = materialsRepositoryPort
                .findAllByIds(combinedMaterialIds.stream().toList()).stream()
                .collect(Collectors.toMap(Material::getId, m -> m));

        return borrows.stream().map(borrow -> {
            Usuario user = usersById.getOrDefault(borrow.getUsuarioId(), new Usuario());
            Material material = materialsById.getOrDefault(borrow.getResourceId(), new Material());

            BorrowReadDTO dto = borrowDtoMapper.toReadDto(borrow, user, material);

            // Ensure amount is consistent with domain calculation
            try {
                dto.setAmount(borrow.calculateTotalAmount());
            } catch (Exception ignore) {
            }

            // Post-process details: set borrowId and enrich materialName using
            // batch-fetched materials
            if (dto.getDetails() != null) {
                dto.getDetails().forEach(detailDto -> {
                    if (detailDto.getBorrowId() == null) {
                        detailDto.setBorrowId(borrow.getId());
                    }
                    com.techmate.techmate.domain.model.Material detailMat = materialsById
                            .get(detailDto.getMaterialsId());
                    if (detailMat != null) {
                        detailDto.setMaterialName(detailMat.getName());
                    }
                });
            }

            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BorrowReadDTO> getByStatus(com.techmate.techmate.entity.Status status) {
        return getAll().stream().filter(d -> d.getStatus() == status).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BorrowReadDTO> getByDateRange(Date start, Date end) {
        return getAll().stream()
                .filter(d -> d.getDate() != null && !d.getDate().before(start) && !d.getDate().after(end))
                .collect(Collectors.toList());
    }
}
