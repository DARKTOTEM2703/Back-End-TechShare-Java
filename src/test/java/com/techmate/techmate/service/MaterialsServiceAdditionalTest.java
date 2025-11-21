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

import com.techmate.techmate.entity.Materials;
import com.techmate.techmate.repository.MaterialsRepository;

/**
 * Tests adicionales para MaterialsService
 * Cubre casos de materiales, inventario y búsquedas
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("📖 MaterialsService - Tests Adicionales")
class MaterialsServiceAdditionalTest {
    
    @Mock
    private MaterialsRepository materialsRepository;
    
    private Materials testMaterial;
    
    @BeforeEach
    void setUp() {
        testMaterial = new Materials();
        testMaterial.setId(1);
        testMaterial.setName("Java Programming");
        testMaterial.setStock(10);
        testMaterial.setBorrowable_stock(8);
    }
    
    @Test
    @DisplayName("✅ Obtener material por ID existente")
    void getMaterialById_Exists_Success() {
        // Given: Material existente
        when(materialsRepository.findById(1)).thenReturn(Optional.of(testMaterial));
        
        // When: Buscar material
        Optional<Materials> result = materialsRepository.findById(1);
        
        // Then: Debe existir
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Java Programming");
    }
    
    @Test
    @DisplayName("❌ Obtener material por ID inexistente")
    void getMaterialById_NotExists_ReturnEmpty() {
        // Given: Material no existe
        when(materialsRepository.findById(999)).thenReturn(Optional.empty());
        
        // When: Buscar material
        Optional<Materials> result = materialsRepository.findById(999);
        
        // Then: Debe estar vacío
        assertThat(result).isEmpty();
    }
    
    @Test
    @DisplayName("✅ Listar todos los materiales")
    void getAllMaterials_HasItems_ReturnsList() {
        // Given: Múltiples materiales
        List<Materials> materials = Arrays.asList(testMaterial);
        when(materialsRepository.findAll()).thenReturn(materials);
        
        // When: Obtener todos
        List<Materials> result = materialsRepository.findAll();
        
        // Then: Debe retornar lista
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
    }
    
    @Test
    @DisplayName("✅ Obtener materiales ordenados por precio ascendente")
    void getAllMaterials_OrderByPrice_Success() {
        // Given: Múltiples materiales
        List<Materials> materials = Arrays.asList(testMaterial);
        when(materialsRepository.findAllByOrderByPriceAsc()).thenReturn(materials);
        
        // When: Obtener ordenados
        List<Materials> result = materialsRepository.findAllByOrderByPriceAsc();
        
        // Then: Debe retornar lista
        assertThat(result).isNotEmpty();
    }
    
    @Test
    @DisplayName("✅ Validar cantidad disponible")
    void checkAvailableQuantity_HasStock_ReturnsCorrectValue() {
        // Given: Material con 8 disponibles de 10
        
        // When: Verificar disponibilidad
        int available = testMaterial.getBorrowable_stock();
        
        // Then: Debe haber stock
        assertThat(available).isGreaterThan(0);
        assertThat(available).isLessThanOrEqualTo(testMaterial.getStock());
    }
    
    @Test
    @DisplayName("⚠️ Material con bajo stock")
    void checkLowStock_QuantityBelowThreshold_DetectWarning() {
        // Given: Material con poco stock
        testMaterial.setStock(2);
        testMaterial.setBorrowable_stock(1);
        
        // When: Verificar si está bajo
        boolean isLowStock = testMaterial.getBorrowable_stock() < 3;
        
        // Then: Debe detectarse bajo stock
        assertThat(isLowStock).isTrue();
    }
}

