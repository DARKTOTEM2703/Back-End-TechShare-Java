package com.techmate.techmate.service.borrow.mapper;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import com.techmate.techmate.dto.BorrowDTO;
import com.techmate.techmate.dto.BorrowResponse;
import com.techmate.techmate.dto.DetailsBorrowResponse;
import com.techmate.techmate.dto.DetailsBorrowDTO;
import com.techmate.techmate.entity.Borrow;
import com.techmate.techmate.entity.DetailsBorrow;

/**
 * Unified BorrowMapper: provides conversions between Entity, internal DTO and API Response.
 */
@Component
public class BorrowMapper {

    // Controller-friendly: BorrowDTO -> BorrowResponse
    public BorrowResponse toResponse(BorrowDTO b) {
        if (b == null) return null;

        List<DetailsBorrowResponse> details = null;
        if (b.getDetails() != null) {
            details = b.getDetails().stream()
                    .map(d -> new DetailsBorrowResponse(d.getId(), d.getQuantity(), d.getUnitPrice(), d.getTotalPrice(), d.getId()))
                    .collect(Collectors.toList());
        }

        return new BorrowResponse(b.getId(), b.getDate(), b.getStartDate(), b.getEndDate(), b.getReturnDate(), b.getStatus(), b.getAmount(), b.getUsuarioId(), b.getUsuarioName(), b.getAdminId(), b.getAdminName(), details);
    }

    // Entity -> DTO
    public BorrowDTO toDTO(Borrow borrow) {
        if (borrow == null) return null;
        BorrowDTO dto = new BorrowDTO();
        dto.setId(borrow.getId());
        dto.setDate(borrow.getDate());
        dto.setStatus(borrow.getStatus());
        dto.setAmount(borrow.getAmount());
        // Nota: startDate y admin ya no están en la Entity (fueron removidos)
        dto.setEndDate(borrow.getEndDate());
        dto.setReturnDate(borrow.getReturnDate());

        if (borrow.getUsuario() != null) {
            dto.setUsuarioId(borrow.getUsuario().getId());
            dto.setUsuarioName(borrow.getUsuario().getUser_name());
        }

        if (borrow.getDetails() != null) {
            dto.setDetails(borrow.getDetails().stream().map(this::detailsToDTO).collect(Collectors.toList()));
        }

        return dto;
    }
    // DTO -> Entity
    public Borrow toEntity(BorrowDTO borrowDTO) {
        if (borrowDTO == null) return null;
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

    // Details mapping
    public DetailsBorrowDTO detailsToDTO(DetailsBorrow detailsBorrow) {
        if (detailsBorrow == null) return null;
        DetailsBorrowDTO dto = new DetailsBorrowDTO();
        dto.setId(detailsBorrow.getId());
        dto.setQuantity(detailsBorrow.getQuantity());
        dto.setUnitPrice(detailsBorrow.getUnitPrice());
        dto.setTotalPrice(detailsBorrow.getTotalPrice());
        if (detailsBorrow.getMaterials() != null) dto.setId(detailsBorrow.getMaterials().getId());
        if (detailsBorrow.getBorrow() != null) dto.setId(detailsBorrow.getBorrow().getId());
        return dto;
    }

    public DetailsBorrow detailsToEntity(DetailsBorrowDTO detailsDTO) {
        if (detailsDTO == null) return null;
        DetailsBorrow d = new DetailsBorrow();
        d.setId(detailsDTO.getId());
        d.setQuantity(detailsDTO.getQuantity());
        d.setUnitPrice(detailsDTO.getUnitPrice());
        d.setTotalPrice(detailsDTO.getTotalPrice());
        return d;
    }
}

