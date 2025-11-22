// BorrowUserController.java
package com.techmate.techmate.controller.User;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.type.TypeReference;

import jakarta.servlet.http.HttpServletRequest;

import com.techmate.techmate.service.User.BorrowUserService;
import com.techmate.techmate.dto.BorrowDTO;
import com.techmate.techmate.dto.DetailsBorrowDTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

// CORS configurado globalmente en WebSecurityConfig - no necesita @CrossOrigin aquí
@RestController
@RequestMapping("/borrow")
public class BorrowUserController {

    private final BorrowUserService borrowUserService;

    public BorrowUserController(BorrowUserService borrowUserService) {
        this.borrowUserService = borrowUserService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createBorrow(
            @RequestParam("details") String detailsJson,
            HttpServletRequest request // Pasamos los detalles como un String JSON
    ) throws Exception {
        BorrowDTO borrowDTO = new BorrowDTO();
        borrowDTO.setDate(new Date());

        String token = request.getHeader("Authorization");
        Integer userId = null;
        List<Integer> roles = new ArrayList<>();

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                userId = borrowUserService.getUserIdFromToken(token);
                borrowDTO.setUsuarioId(userId);

                Optional<List<Integer>> rolesOptional = borrowUserService.getRolesFromToken(token);
                if (rolesOptional.isPresent()) {
                    roles = rolesOptional.get();
                }
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
        }

        List<DetailsBorrowDTO> details = convertJsonToDetailsList(detailsJson);
        borrowDTO.setDetails(details);

        BorrowDTO createdBorrow = borrowUserService.createBorrowDTO(borrowDTO, roles);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBorrow);
    }

    private List<DetailsBorrowDTO> convertJsonToDetailsList(String detailsJson) {
        com.fasterxml.jackson.databind.ObjectMapper objectMapper = com.techmate.techmate.config.JacksonConfig
                .objectMapper();
        try {
            return objectMapper.readValue(detailsJson, new TypeReference<List<DetailsBorrowDTO>>() {
            });
        } catch (Exception e) {
            throw new RuntimeException("Error al convertir el JSON a la lista de detalles.", e);
        }
    }

    @GetMapping("/user/borrows")
    public ResponseEntity<List<BorrowDTO>> getAllBorrows(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        Integer userId = null;

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);

            userId = borrowUserService.getUserIdFromToken(token);
            List<BorrowDTO> borrows = borrowUserService.getAllBorrowsByUserId(userId);
            return ResponseEntity.ok(borrows);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
