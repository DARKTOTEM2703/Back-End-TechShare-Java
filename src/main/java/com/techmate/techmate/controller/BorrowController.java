package com.techmate.techmate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.*;

import com.techmate.techmate.dto.BorrowDTO;
import com.techmate.techmate.dto.BorrowReadDTO;
import com.techmate.techmate.entity.Status;
import com.techmate.techmate.service.BorrowService;
import com.techmate.techmate.application.usecase.GetBorrowsUseCase;
import com.techmate.techmate.application.usecase.UpdateBorrowStatusUseCase;

import jakarta.servlet.http.HttpServletRequest;

// CORS configurado globalmente en WebSecurityConfig - no necesita @CrossOrigin aquí
@RestController
@RequestMapping("admin/borrow")
@PreAuthorize("hasRole('ADMIN')")
public class BorrowController {
    private final BorrowService borrowService;
    private final UpdateBorrowStatusUseCase updateBorrowStatusUseCase;
    private final GetBorrowsUseCase getBorrowsUseCase;

    public BorrowController(BorrowService borrowService, UpdateBorrowStatusUseCase updateBorrowStatusUseCase,
            GetBorrowsUseCase getBorrowsUseCase) {
        this.borrowService = borrowService;
        this.updateBorrowStatusUseCase = updateBorrowStatusUseCase;
        this.getBorrowsUseCase = getBorrowsUseCase;
    }

    // Actualizar el estado de un préstamo
    @PutMapping("/update/{borrowId}")
    public ResponseEntity<?> updateBorrowStatus(
            @PathVariable Integer borrowId,
            @RequestParam("status") Status newStatus,
            HttpServletRequest request) throws Exception {

        BorrowDTO borrowDTO = new BorrowDTO();

        borrowDTO.setStartDate(new Date());

        String token = request.getHeader("Authorization");
        Integer adminId = null;

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);

            adminId = borrowService.getUserIdFromToken(token);
            System.out.println("Id de usuario extraído del token:  " + adminId);
        }

        // Delegate to application use case (Onion Architecture)
        updateBorrowStatusUseCase.execute(borrowId, newStatus, adminId);
        return ResponseEntity.ok("Estado del préstamo actualizado correctamente.");
    }

    @GetMapping("/all")
    public ResponseEntity<List<BorrowReadDTO>> getAllBorrow() {

        List<BorrowReadDTO> response = getBorrowsUseCase.getAll();
        if (response.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(response);
    }

}
