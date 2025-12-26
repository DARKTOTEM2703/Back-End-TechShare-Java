// BorrowUserServiceImp.java
package com.techmate.techmate.service.User.Impl;

import java.util.stream.Collectors;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techmate.techmate.dto.BorrowDTO;
import com.techmate.techmate.dto.DetailsBorrowDTO;
import com.techmate.techmate.entity.Borrow;
import com.techmate.techmate.entity.DetailsBorrow;
import com.techmate.techmate.entity.Materials;
import com.techmate.techmate.entity.RoleMaterials;
import com.techmate.techmate.entity.Status;
import com.techmate.techmate.entity.Usuario;
import com.techmate.techmate.repository.*;
import com.techmate.techmate.security.TokenUtils;
import com.techmate.techmate.service.User.BorrowUserService;
import com.techmate.techmate.service.borrow.mapper.BorrowMapper;

@Service
public class BorrowUserServiceImp implements BorrowUserService {

    private final com.techmate.techmate.application.usecase.CreateBorrowUseCase createBorrowUseCase;
    private final BorrowMapper borrowMapper;
    private final com.techmate.techmate.repository.BorrowRepository borrowRepository;

    public BorrowUserServiceImp(com.techmate.techmate.application.usecase.CreateBorrowUseCase createBorrowUseCase,
            BorrowMapper borrowMapper, com.techmate.techmate.repository.BorrowRepository borrowRepository) {
        this.createBorrowUseCase = createBorrowUseCase;
        this.borrowMapper = borrowMapper;
        this.borrowRepository = borrowRepository;
    }

    @Override
    @Transactional
    public BorrowDTO createBorrowDTO(BorrowDTO borrowDTO, List<Integer> roles) throws Exception {
        return createBorrowUseCase.execute(borrowDTO, roles);
    }

    @Override
    public Integer getUserIdFromToken(String token) {
        return TokenUtils.getUserIdFromToken(token);
    }

    @Override
    public List<BorrowDTO> getAllBorrowsByUserId(Integer userId) {
        List<Borrow> borrows = borrowRepository.findByUsuarioId(userId);
        return borrows.stream()
                .map(borrowMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<List<Integer>> getRolesFromToken(String token) {
        return TokenUtils.getRolesFromToken(token);
    }
}
