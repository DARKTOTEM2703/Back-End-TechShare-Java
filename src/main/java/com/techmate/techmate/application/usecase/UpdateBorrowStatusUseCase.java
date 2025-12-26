package com.techmate.techmate.application.usecase;

import com.techmate.techmate.domain.model.Borrow;
import com.techmate.techmate.domain.port.out.BorrowRepositoryPort;
import com.techmate.techmate.entity.Status;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UpdateBorrowStatusUseCase {

    private final BorrowRepositoryPort borrowRepositoryPort;

    public UpdateBorrowStatusUseCase(BorrowRepositoryPort borrowRepositoryPort) {
        this.borrowRepositoryPort = borrowRepositoryPort;
    }

    @Transactional
    public void execute(Integer borrowId, Status newStatus, Integer adminId) throws Exception {
        if (borrowId == null || borrowId <= 0) {
            throw new IllegalArgumentException("ID del préstamo inválido");
        }

        Optional<Borrow> maybe = borrowRepositoryPort.findById(borrowId);
        if (maybe.isEmpty()) {
            throw new IllegalArgumentException("Préstamo no encontrado: " + borrowId);
        }

        Borrow borrow = maybe.get();

        try {
            borrow.changeStatus(newStatus, adminId);
            borrowRepositoryPort.save(borrow);
        } catch (Exception e) {
            throw e;
        }
    }
}
