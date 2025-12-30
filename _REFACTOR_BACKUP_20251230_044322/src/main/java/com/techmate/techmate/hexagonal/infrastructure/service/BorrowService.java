package com.techmate.techmate.hexagonal.infrastructure.service;

import java.util.*;

import com.techmate.techmate.hexagonal.infrastructure.dto.BorrowDTO;
import com.techmate.techmate.hexagonal.domain.entity.Status;

public interface BorrowService {
    void updateBorrowStatus(Integer borrowId, Status newStatus, Integer adminId) throws Exception;

    List<BorrowDTO> getAllBorrowDTO();

    List<BorrowDTO> getBorrowByStatus(String status);

    List<BorrowDTO> getBorrowByDate(Date startDate, Date endDate);




    
     /*
     * Token
     */
    Integer getUserIdFromToken(String token);

   
}











