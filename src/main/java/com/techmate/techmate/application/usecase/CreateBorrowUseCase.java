package com.techmate.techmate.application.usecase;

import com.techmate.techmate.domain.model.Borrow;
import com.techmate.techmate.domain.model.DetailsBorrow;
import com.techmate.techmate.domain.model.Material;
import com.techmate.techmate.domain.port.out.BorrowRepositoryPort;
import com.techmate.techmate.domain.port.out.DetailsBorrowRepositoryPort;
import com.techmate.techmate.domain.port.out.MaterialsRepositoryPort;
import com.techmate.techmate.dto.BorrowDTO;
import com.techmate.techmate.service.borrow.mapper.BorrowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CreateBorrowUseCase {

    private final BorrowRepositoryPort borrowRepositoryPort;
    private final MaterialsRepositoryPort materialsRepositoryPort;
    private final DetailsBorrowRepositoryPort detailsBorrowRepositoryPort;
    private final BorrowMapper borrowMapper;

    public CreateBorrowUseCase(BorrowRepositoryPort borrowRepositoryPort,
            MaterialsRepositoryPort materialsRepositoryPort,
            DetailsBorrowRepositoryPort detailsBorrowRepositoryPort,
            BorrowMapper borrowMapper) {
        this.borrowRepositoryPort = borrowRepositoryPort;
        this.materialsRepositoryPort = materialsRepositoryPort;
        this.detailsBorrowRepositoryPort = detailsBorrowRepositoryPort;
        this.borrowMapper = borrowMapper;
    }

    @Transactional
    public BorrowDTO execute(BorrowDTO dto, List<Integer> roles) throws Exception {
        // Basic validation
        if (roles == null || roles.isEmpty()) {
            throw new IllegalArgumentException("USER_NO_ROLES");
        }

        // Convert DTO to domain Borrow (partial)
        Borrow borrow = new Borrow();
        borrow.setDate(dto.getDate());
        borrow.setStatus(com.techmate.techmate.entity.Status.PENDING);
        borrow.setUsuarioId(dto.getUsuarioId());
        borrow.setAmount(BigDecimal.ZERO);

        // Save borrow (initial)
        Borrow saved = borrowRepositoryPort.save(borrow);

        // For each detail, validate material, stock and roles
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (com.techmate.techmate.dto.DetailsBorrowDTO d : dto.getDetails()) {
            Material m = materialsRepositoryPort.findById(d.getId())
                    .orElseThrow(() -> new Exception("MATERIAL_NOT_FOUND"));

            if (m.getBorrowableStock() < d.getQuantity()) {
                throw new Exception("BORROW_INSUFFICIENT_STOCK");
            }

            DetailsBorrow db = new DetailsBorrow();
            db.setQuantity(d.getQuantity());
            db.setMaterialsId(m.getId());
            db.setUnitPrice(m.getPrice());
            db.setTotalPrice(m.getPrice() != null ? m.getPrice().multiply(BigDecimal.valueOf(d.getQuantity()))
                    : BigDecimal.ZERO);

            detailsBorrowRepositoryPort.save(db);

            // decrease stock and save material
            m.setBorrowableStock(m.getBorrowableStock() - d.getQuantity());
            materialsRepositoryPort.save(m);

            totalAmount = totalAmount.add(db.getTotalPrice());
        }

        saved.setAmount(totalAmount);
        saved = borrowRepositoryPort.save(saved);

        // Return mapped DTO using existing mapper
        com.techmate.techmate.entity.Borrow jpa = new com.techmate.techmate.entity.Borrow();
        jpa.setId(saved.getId());
        jpa.setDate(saved.getDate());
        jpa.setAmount(saved.getAmount());
        return borrowMapper.toDTO(jpa);
    }
}
