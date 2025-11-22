package com.techmate.techmate.service.User;

import java.util.*;

import com.techmate.techmate.dto.BorrowDTO;

public interface BorrowUserService {

    BorrowDTO createBorrowDTO(BorrowDTO borrowDTO, List<Integer> roles) throws Exception;

    List<BorrowDTO> getAllBorrowsByUserId(Integer userId);

    /*
     * Token
     */
    Integer getUserIdFromToken(String token);

    Optional<List<Integer>> getRolesFromToken(String token);
}
