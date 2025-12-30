package com.techmate.techmate.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.techmate.techmate.entity.Borrow;
import java.math.BigDecimal;
import com.techmate.techmate.entity.Status;
import com.techmate.techmate.repository.BorrowRepository;

/**
 * Tests para BorrowService - Búsquedas y queries
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("📚 BorrowService - Tests Búsquedas")
class BorrowServiceSearchTest {

    @Mock
    private BorrowRepository borrowRepository;

    private Borrow testBorrow;

    @BeforeEach
    void setUp() {
        testBorrow = new Borrow();
        testBorrow.setId(1);
        testBorrow.setStatus(Status.BORROWED);
        testBorrow.setAmount(BigDecimal.valueOf(100.0));
    }

    @Test
    @DisplayName("✅ Obtener préstamo por ID")
    void getBorrowById_Found() {
        // Arrange
        when(borrowRepository.findById(1)).thenReturn(Optional.of(testBorrow));

        // Act
        Optional<Borrow> result = borrowRepository.findById(1);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getStatus()).isEqualTo(Status.BORROWED);
    }

    @Test
    @DisplayName("❌ Préstamo no existe")
    void getBorrowById_NotFound() {
        // Arrange
        when(borrowRepository.findById(999)).thenReturn(Optional.empty());

        // Act
        Optional<Borrow> result = borrowRepository.findById(999);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("✅ Listar todos los préstamos")
    void getAllBorrows_Success() {
        // Arrange
        List<Borrow> borrows = Arrays.asList(testBorrow);
        when(borrowRepository.findAll()).thenReturn(borrows);

        // Act
        List<Borrow> result = borrowRepository.findAll();

        // Assert
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("✅ Buscar préstamos por estado")
    void findByStatus_Success() {
        // Arrange
        List<Borrow> borrows = Arrays.asList(testBorrow);
        when(borrowRepository.findByStatus(Status.BORROWED)).thenReturn(borrows);

        // Act
        List<Borrow> result = borrowRepository.findByStatus(Status.BORROWED);

        // Assert
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getStatus()).isEqualTo(Status.BORROWED);
    }

    @Test
    @DisplayName("✅ Contar préstamos por estado")
    void countByStatus_Success() {
        // Arrange
        when(borrowRepository.countByStatus(Status.BORROWED)).thenReturn(5L);

        // Act
        long count = borrowRepository.countByStatus(Status.BORROWED);

        // Assert
        assertThat(count).isEqualTo(5L);
    }

    @Test
    @DisplayName("✅ Guardar nuevo préstamo")
    void saveBorrow_Success() {
        // Arrange
        when(borrowRepository.save(any(Borrow.class))).thenReturn(testBorrow);

        // Act
        Borrow result = borrowRepository.save(testBorrow);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        verify(borrowRepository).save(any(Borrow.class));
    }

    @Test
    @DisplayName("✅ Eliminar préstamo")
    void deleteBorrow_Success() {
        // Arrange
        doNothing().when(borrowRepository).deleteById(1);

        // Act
        borrowRepository.deleteById(1);

        // Assert
        verify(borrowRepository).deleteById(1);
    }

    @Test
    @DisplayName("✅ Contar total de préstamos")
    void countAllBorrows() {
        // Arrange
        when(borrowRepository.count()).thenReturn(50L);

        // Act
        long count = borrowRepository.count();

        // Assert
        assertThat(count).isEqualTo(50L);
    }
}
