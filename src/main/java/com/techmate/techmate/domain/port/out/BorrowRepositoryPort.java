package com.techmate.techmate.domain.port.out;

import com.techmate.techmate.domain.model.Borrow;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para persistencia de Borrow (Dominio)
 */
public interface BorrowRepositoryPort {

    Optional<Borrow> findById(Integer id);

    List<Borrow> findAll();

    Borrow save(Borrow borrow);

}
