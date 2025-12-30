// BorrowUserServiceImp.java
package com.techmate.techmate.hexagonal.infrastructure.service.User.Impl;

import java.util.stream.Collectors;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techmate.techmate.hexagonal.infrastructure.dto.BorrowDTO;
import com.techmate.techmate.hexagonal.infrastructure.dto.DetailsBorrowDTO;
import com.techmate.techmate.hexagonal.domain.entity.Borrow;
import com.techmate.techmate.hexagonal.domain.entity.DetailsBorrow;
import com.techmate.techmate.hexagonal.domain.entity.Materials;
import com.techmate.techmate.hexagonal.domain.entity.RoleMaterials;
import com.techmate.techmate.hexagonal.domain.entity.Status;
import com.techmate.techmate.hexagonal.domain.entity.Usuario;
import com.techmate.techmate.hexagonal.domain.repository.*;
import com.techmate.techmate.hexagonal.infrastructure.security.TokenUtils;
import com.techmate.techmate.hexagonal.infrastructure.service.User.BorrowUserService;
import com.techmate.techmate.hexagonal.infrastructure.service.borrow.mapper.BorrowMapper;

@Service
public class BorrowUserServiceImp implements BorrowUserService {

    private final BorrowMapper borrowMapper;
    private final com.techmate.techmate.hexagonal.domain.repository.BorrowRepository borrowRepository;

    public BorrowUserServiceImp(BorrowMapper borrowMapper,
            com.techmate.techmate.hexagonal.domain.repository.BorrowRepository borrowRepository) {
        this.borrowMapper = borrowMapper;
        this.borrowRepository = borrowRepository;
    }

    @Override
    @Transactional
    public BorrowDTO createBorrowDTO(BorrowDTO borrowDTO, List<Integer> roles) throws Exception {
        // TODO: Implement with CreateBorrowUseCase when available
        throw new UnsupportedOperationException("CreateBorrowUseCase not yet implemented");
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
