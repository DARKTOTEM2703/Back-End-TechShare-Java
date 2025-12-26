package com.techmate.techmate.infra.adapter.jpa;

import com.techmate.techmate.domain.model.Borrow;
import com.techmate.techmate.domain.port.out.BorrowRepositoryPort;
import com.techmate.techmate.infra.mapper.DomainBorrowMapper;
import com.techmate.techmate.repository.BorrowRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class JpaBorrowRepositoryAdapter implements BorrowRepositoryPort {

    private final BorrowRepository borrowRepository;
    private final DomainBorrowMapper mapper;

    public JpaBorrowRepositoryAdapter(BorrowRepository borrowRepository, DomainBorrowMapper mapper) {
        this.borrowRepository = borrowRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Borrow> findById(Integer id) {
        return borrowRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Borrow> findAll() {
        List<com.techmate.techmate.entity.Borrow> list = borrowRepository.findAll();
        return mapper.toDomainList(list);
    }

    @Override
    public Borrow save(Borrow borrow) {
        com.techmate.techmate.entity.Borrow jpa = mapper.toJpa(borrow);
        com.techmate.techmate.entity.Borrow saved = borrowRepository.save(jpa);
        return mapper.toDomain(saved);
    }
}
