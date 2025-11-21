package com.techmate.techmate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techmate.techmate.entity.DetailsBorrow;

@Repository
public interface DetailsBorrowRepository extends JpaRepository<DetailsBorrow, Integer> {
    // Métodos adicionales si es necesario
}



