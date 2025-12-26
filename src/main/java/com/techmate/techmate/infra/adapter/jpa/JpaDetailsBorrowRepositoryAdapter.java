package com.techmate.techmate.infra.adapter.jpa;

import com.techmate.techmate.domain.model.DetailsBorrow;
import com.techmate.techmate.domain.port.out.DetailsBorrowRepositoryPort;
import com.techmate.techmate.repository.DetailsBorrowRepository;
import org.springframework.stereotype.Component;

@Component
public class JpaDetailsBorrowRepositoryAdapter implements DetailsBorrowRepositoryPort {

    private final DetailsBorrowRepository detailsBorrowRepository;

    public JpaDetailsBorrowRepositoryAdapter(DetailsBorrowRepository detailsBorrowRepository) {
        this.detailsBorrowRepository = detailsBorrowRepository;
    }

    @Override
    public DetailsBorrow save(DetailsBorrow detailsBorrow) {
        com.techmate.techmate.entity.DetailsBorrow j = new com.techmate.techmate.entity.DetailsBorrow();
        j.setId(detailsBorrow.getId());
        j.setQuantity(detailsBorrow.getQuantity());
        j.setUnitPrice(detailsBorrow.getUnitPrice());
        j.setTotalPrice(detailsBorrow.getTotalPrice());
        // materials and borrow relations should be set by caller via JPA entities if
        // needed
        com.techmate.techmate.entity.DetailsBorrow saved = detailsBorrowRepository.save(j);
        DetailsBorrow out = new DetailsBorrow();
        out.setId(saved.getId());
        out.setQuantity(saved.getQuantity());
        out.setUnitPrice(saved.getUnitPrice());
        out.setTotalPrice(saved.getTotalPrice());
        return out;
    }
}
