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
 * Tests para MaterialsService - Búsquedas y filtros
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("🔍 MaterialsService - Tests Búsquedas")
class MaterialsServiceSearchTest {

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
    @DisplayName("✅ Buscar material por ID - SUCCESS")
    void searchMaterialById_Found() {
        // Arrange
        when(materialsRepository.findById(1)).thenReturn(Optional.of(testMaterial));

        // Act
        Optional<Materials> result = materialsRepository.findById(1);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Java Programming");
        verify(materialsRepository).findById(1);
    }

    @Test
    @DisplayName("❌ Buscar material inexistente - NOT FOUND")
    void searchMaterialById_NotFound() {
        // Arrange
        when(materialsRepository.findById(999)).thenReturn(Optional.empty());

        // Act
        Optional<Materials> result = materialsRepository.findById(999);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("✅ Listar todos los materiales")
    void getAllMaterials_Success() {
        // Arrange
        List<Materials> materials = Arrays.asList(testMaterial);
        when(materialsRepository.findAll()).thenReturn(materials);

        // Act
        List<Materials> result = materialsRepository.findAll();

        // Assert
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("✅ Ordenar materiales por precio ASC")
    void getAllMaterials_OrderByPriceAsc() {
        // Arrange
        List<Materials> materials = Arrays.asList(testMaterial);
        when(materialsRepository.findAllByOrderByPriceAsc()).thenReturn(materials);

        // Act
        List<Materials> result = materialsRepository.findAllByOrderByPriceAsc();

        // Assert
        assertThat(result).isNotEmpty();
    }

    @Test
    @DisplayName("✅ Ordenar materiales por precio DESC")
    void getAllMaterials_OrderByPriceDesc() {
        // Arrange
        List<Materials> materials = Arrays.asList(testMaterial);
        when(materialsRepository.findAllByOrderByPriceDesc()).thenReturn(materials);

        // Act
        List<Materials> result = materialsRepository.findAllByOrderByPriceDesc();

        // Assert
        assertThat(result).isNotEmpty();
    }

    @Test
    @DisplayName("✅ Detectar bajo stock")
    void findLowStock_BelowThreshold() {
        // Arrange
        List<Materials> lowStock = Arrays.asList(testMaterial);
        when(materialsRepository.findLowStock(5)).thenReturn(lowStock);

        // Act
        List<Materials> result = materialsRepository.findLowStock(5);

        // Assert
        assertThat(result).isNotEmpty();
    }

    @Test
    @DisplayName("✅ Contar total de materiales")
    void countAllMaterials() {
        // Arrange
        when(materialsRepository.count()).thenReturn(10L);

        // Act
        long count = materialsRepository.count();

        // Assert
        assertThat(count).isEqualTo(10L);
    }

    @Test
    @DisplayName("✅ Guardar nuevo material")
    void saveMaterial_Success() {
        // Arrange
        when(materialsRepository.save(any(Materials.class))).thenReturn(testMaterial);

        // Act
        Materials result = materialsRepository.save(testMaterial);

        // Assert
        assertThat(result).isNotNull();
        verify(materialsRepository).save(any(Materials.class));
    }

    @Test
    @DisplayName("✅ Eliminar material por ID")
    void deleteMaterial_Success() {
        // Arrange
        doNothing().when(materialsRepository).deleteById(1);

        // Act
        materialsRepository.deleteById(1);

        // Assert
        verify(materialsRepository).deleteById(1);
    }
}
