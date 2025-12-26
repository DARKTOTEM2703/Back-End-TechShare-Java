package com.techmate.techmate.infra.mapper;

import com.techmate.techmate.domain.model.Borrow;
import com.techmate.techmate.dto.BorrowReadDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DomainToDtoMapper {

    public BorrowReadDTO toReadDTO(Borrow b) {
        if (b == null)
            return null;
        BorrowReadDTO dto = new BorrowReadDTO();
        dto.setId(b.getId());
        dto.setDate(b.getDate());
        dto.setStartDate(b.getStartDate());
        dto.setEndDate(b.getEndDate());
        dto.setReturnDate(b.getReturnDate());
        dto.setStatus(b.getStatus());
        dto.setAmount(b.getAmount());
        dto.setUsuarioId(b.getUsuarioId());
        // details mapping omitted (legacy DetailsBorrow DTO conversion required)
        dto.setDetails(new ArrayList<>());
        return dto;
    }

    public List<BorrowReadDTO> toReadDTOList(List<Borrow> list) {
        List<BorrowReadDTO> out = new ArrayList<>();
        if (list == null)
            return out;
        for (Borrow b : list)
            out.add(toReadDTO(b));
        return out;
    }
}
