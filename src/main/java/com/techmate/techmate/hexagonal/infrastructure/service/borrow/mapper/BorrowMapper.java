package com.techmate.techmate.hexagonal.infrastructure.service.borrow.mapper;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import com.techmate.techmate.hexagonal.infrastructure.dto.BorrowDTO;
import com.techmate.techmate.hexagonal.infrastructure.dto.BorrowCreateDTO;
import com.techmate.techmate.hexagonal.infrastructure.dto.BorrowReadDTO;
import com.techmate.techmate.hexagonal.infrastructure.dto.BorrowResponse;
import com.techmate.techmate.hexagonal.infrastructure.dto.DetailsBorrowResponse;
import com.techmate.techmate.hexagonal.infrastructure.dto.DetailsBorrowDTO;
import com.techmate.techmate.hexagonal.domain.entity.Borrow;
import com.techmate.techmate.hexagonal.domain.entity.DetailsBorrow;

/**
 * Unified BorrowMapper: provides conversions between Entity, internal DTO and
 * API Response.
 */
@Component
public class BorrowMapper {

    // Controller-friendly: BorrowDTO -> BorrowResponse
    public BorrowResponse toResponse(BorrowDTO b) {
        if (b == null)
            return null;

        List<DetailsBorrowResponse> details = null;
        if (b.getDetails() != null) {
            details = b.getDetails().stream()
                    .map(d -> new DetailsBorrowResponse(d.getId(), d.getQuantity(), d.getUnitPrice(), d.getTotalPrice(),
                            d.getId()))
                    .collect(Collectors.toList());
        }

        return new BorrowResponse(b.getId(), b.getDate(), b.getStartDate(), b.getEndDate(), b.getReturnDate(),
                b.getStatus(), b.getAmount(), b.getUsuarioId(), b.getUsuarioName(), b.getAdminId(), b.getAdminName(),
                details);
    }

    // Entity -> DTO
    public BorrowDTO toDTO(Borrow borrow) {
        if (borrow == null)
            return null;
        BorrowDTO dto = new BorrowDTO();
        dto.setId(borrow.getId());
        dto.setDate(borrow.getDate());
        dto.setStatus(borrow.getStatus());
        dto.setAmount(borrow.getAmount());
        dto.setStartDate(borrow.getStartDate());
        dto.setEndDate(borrow.getEndDate());
        dto.setReturnDate(borrow.getReturnDate());

        if (borrow.getUsuario() != null) {
            dto.setUsuarioId(borrow.getUsuario().getId());
            dto.setUsuarioName(borrow.getUsuario().getUser_name());
        }

        if (borrow.getAdmin() != null) {
            dto.setAdminId(borrow.getAdmin().getId());
            dto.setAdminName(borrow.getAdmin().getUser_name());
        }

        if (borrow.getDetails() != null) {
            dto.setDetails(borrow.getDetails().stream().map(this::detailsToDTO).collect(Collectors.toList()));
        }

        return dto;
    }

    // DTO -> Entity
    public Borrow toEntity(BorrowDTO borrowDTO) {
        if (borrowDTO == null)
            return null;
        Borrow borrow = new Borrow();
        borrow.setId(borrowDTO.getId());
        borrow.setDate(borrowDTO.getDate());
        borrow.setStatus(borrowDTO.getStatus());
        borrow.setAmount(borrowDTO.getAmount());
        // Nota: startDate no se persiste (fue removido de Entity)
        borrow.setEndDate(borrowDTO.getEndDate());
        borrow.setReturnDate(borrowDTO.getReturnDate());
        // Note: relationships should be set by services when necessary
        return borrow;
    }

    // BorrowCreateDTO -> Entity (for create/update operations)
    public Borrow fromCreateDTO(BorrowCreateDTO createDTO) {
        if (createDTO == null)
            return null;
        Borrow borrow = new Borrow();
        borrow.setId(createDTO.getId());
        borrow.setEndDate(createDTO.getEndDate());
        // Note: relationships (usuario, admin) should be set by services
        // Note: details should be converted and set by services
        return borrow;
    }

    // Entity -> BorrowReadDTO (for read operations)
    public BorrowReadDTO toReadDTO(Borrow borrow) {
        if (borrow == null)
            return null;
        BorrowReadDTO dto = new BorrowReadDTO();
        dto.setId(borrow.getId());
        dto.setDate(borrow.getDate());
        dto.setEndDate(borrow.getEndDate());
        dto.setReturnDate(borrow.getReturnDate());
        dto.setStatus(borrow.getStatus());
        dto.setAmount(borrow.getAmount());

        // Campos calculados/transient
        if (borrow.getStartDate() != null) {
            dto.setStartDate(borrow.getStartDate());
        }

        // Datos del usuario
        if (borrow.getUsuario() != null) {
            dto.setUsuarioId(borrow.getUsuario().getId());
            dto.setUsuarioName(borrow.getUsuario().getUser_name());
        }

        // Datos del admin (campo transient)
        if (borrow.getAdmin() != null) {
            dto.setAdminId(borrow.getAdmin().getId());
            dto.setAdminName(borrow.getAdmin().getUser_name());
        }

        // Detalles
        if (borrow.getDetails() != null) {
            dto.setDetails(borrow.getDetails().stream()
                    .map(this::detailsToDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    // Details mapping
    public DetailsBorrowDTO detailsToDTO(DetailsBorrow detailsBorrow) {
        if (detailsBorrow == null)
            return null;
        DetailsBorrowDTO dto = new DetailsBorrowDTO();
        dto.setId(detailsBorrow.getId());
        dto.setQuantity(detailsBorrow.getQuantity());
        dto.setUnitPrice(detailsBorrow.getUnitPrice());
        dto.setTotalPrice(detailsBorrow.getTotalPrice());
        if (detailsBorrow.getMaterials() != null) {
            dto.setMaterialsId(detailsBorrow.getMaterials().getId());
        }
        if (detailsBorrow.getBorrow() != null) {
            dto.setBorrowId(detailsBorrow.getBorrow().getId());
        }
        return dto;
    }

    public DetailsBorrow detailsToEntity(DetailsBorrowDTO detailsDTO) {
        if (detailsDTO == null)
            return null;
        DetailsBorrow d = new DetailsBorrow();
        d.setId(detailsDTO.getId());
        d.setQuantity(detailsDTO.getQuantity());
        d.setUnitPrice(detailsDTO.getUnitPrice());
        d.setTotalPrice(detailsDTO.getTotalPrice());
        return d;
    }
}






