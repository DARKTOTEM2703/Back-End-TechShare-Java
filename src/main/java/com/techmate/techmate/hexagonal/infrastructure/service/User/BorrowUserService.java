package com.techmate.techmate.hexagonal.infrastructure.service.User;

import java.util.*;

import com.techmate.techmate.hexagonal.infrastructure.dto.BorrowDTO;

public interface BorrowUserService {

    BorrowDTO createBorrowDTO(BorrowDTO borrowDTO, List<Integer> roles) throws Exception;

    List<BorrowDTO> getAllBorrowsByUserId(Integer userId);

    /*
     * Token
     */
    Integer getUserIdFromToken(String token);

    Optional<List<Integer>> getRolesFromToken(String token);
}







