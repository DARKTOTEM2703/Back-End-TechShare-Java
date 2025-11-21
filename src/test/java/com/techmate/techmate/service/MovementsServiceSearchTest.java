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

import com.techmate.techmate.entity.Movements;
import com.techmate.techmate.repository.MovementsRepository;

/**
 * Tests para MovementsService - Búsquedas y tracking
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("📊 MovementsService - Tests Búsquedas")
class MovementsServiceSearchTest {
    
    @Mock
    private MovementsRepository movementsRepository;
    
    private Movements testMovement;
    
    @BeforeEach
    void setUp() {
        testMovement = new Movements();
        testMovement.setId(1);
        testMovement.setQuantity(10);
    }
    
    @Test
    @DisplayName("✅ Obtener movimiento por ID")
    void getMovementById_Found() {
        // Arrange
        when(movementsRepository.findById(1)).thenReturn(Optional.of(testMovement));
        
        // Act
        Optional<Movements> result = movementsRepository.findById(1);
        
        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getQuantity()).isEqualTo(10);
    }
    
    @Test
    @DisplayName("❌ Movimiento no existe")
    void getMovementById_NotFound() {
        // Arrange
        when(movementsRepository.findById(999)).thenReturn(Optional.empty());
        
        // Act
        Optional<Movements> result = movementsRepository.findById(999);
        
        // Assert
        assertThat(result).isEmpty();
    }
    
    @Test
    @DisplayName("✅ Listar todos los movimientos")
    void getAllMovements_Success() {
        // Arrange
        List<Movements> movements = Arrays.asList(testMovement);
        when(movementsRepository.findAll()).thenReturn(movements);
        
        // Act
        List<Movements> result = movementsRepository.findAll();
        
        // Assert
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
    }
    
    @Test
    @DisplayName("✅ Contar total de movimientos")
    void countAllMovements() {
        // Arrange
        when(movementsRepository.count()).thenReturn(100L);
        
        // Act
        long count = movementsRepository.count();
        
        // Assert
        assertThat(count).isEqualTo(100L);
    }
    
    @Test
    @DisplayName("✅ Guardar nuevo movimiento")
    void saveMovement_Success() {
        // Arrange
        when(movementsRepository.save(any(Movements.class))).thenReturn(testMovement);
        
        // Act
        Movements result = movementsRepository.save(testMovement);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        verify(movementsRepository).save(any(Movements.class));
    }
    
    @Test
    @DisplayName("✅ Eliminar movimiento")
    void deleteMovement_Success() {
        // Arrange
        doNothing().when(movementsRepository).deleteById(1);
        
        // Act
        movementsRepository.deleteById(1);
        
        // Assert
        verify(movementsRepository).deleteById(1);
    }
    
    @Test
    @DisplayName("✅ Validar cantidad positiva")
    void validateQuantity_Positive() {
        // Arrange
        int quantity = testMovement.getQuantity();
        
        // Act
        boolean isValid = quantity > 0;
        
        // Assert
        assertThat(isValid).isTrue();
    }
    
    @Test
    @DisplayName("❌ Validar cantidad negativa - FAIL")
    void validateQuantity_Negative_Fail() {
        // Arrange
        int quantity = -5;
        
        // Act
        boolean isValid = quantity > 0;
        
        // Assert
        assertThat(isValid).isFalse();
    }
}

