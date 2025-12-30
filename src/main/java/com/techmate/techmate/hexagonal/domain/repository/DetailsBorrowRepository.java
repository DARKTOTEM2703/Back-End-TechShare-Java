package com.techmate.techmate.hexagonal.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techmate.techmate.hexagonal.domain.entity.DetailsBorrow;

@Repository
public interface DetailsBorrowRepository extends JpaRepository<DetailsBorrow, Integer> {
    // Métodos adicionales si es necesario
}





