package com.techmate.techmate.infra.mapper;

import com.techmate.techmate.domain.model.Borrow;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapper entre la entidad JPA y el modelo de dominio.
 */
@Component
public class DomainBorrowMapper {

    public Borrow toDomain(com.techmate.techmate.entity.Borrow jpa) {
        if (jpa == null)
            return null;
        Borrow d = new Borrow();
        d.setId(jpa.getId());
        d.setDate(jpa.getDate());
        d.setStartDate(jpa.getStartDate());
        d.setEndDate(jpa.getEndDate());
        d.setReturnDate(jpa.getReturnDate());
        d.setStatus(jpa.getStatus());
        if (jpa.getUsuario() != null) {
            d.setUsuarioId(jpa.getUsuario().getId());
        }
        d.setAmount(jpa.getAmount());
        return d;
    }

    public com.techmate.techmate.entity.Borrow toJpa(Borrow d) {
        if (d == null)
            return null;
        com.techmate.techmate.entity.Borrow jpa = new com.techmate.techmate.entity.Borrow();
        jpa.setId(d.getId());
        jpa.setDate(d.getDate());
        jpa.setStartDate(d.getStartDate());
        jpa.setEndDate(d.getEndDate());
        jpa.setReturnDate(d.getReturnDate());
        jpa.setStatus(d.getStatus());
        jpa.setAmount(d.getAmount());
        // Note: usuario and details must be handled by caller if needed
        return jpa;
    }

    public List<Borrow> toDomainList(List<com.techmate.techmate.entity.Borrow> list) {
        List<Borrow> out = new ArrayList<>();
        if (list == null)
            return out;
        for (com.techmate.techmate.entity.Borrow b : list) {
            out.add(toDomain(b));
        }
        return out;
    }
}
