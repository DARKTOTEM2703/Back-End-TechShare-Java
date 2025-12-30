package com.techmate.techmate.service.materials.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.techmate.techmate.entity.Materials;
import com.techmate.techmate.repository.MaterialsRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class MaterialsStockManagerTest {

    @Mock
    MaterialsRepository materialsRepository;

    @InjectMocks
    MaterialsStockManager stockManager;

    @Test
    void getAvailableStock_existing_returnsValue() {
        Materials m = new Materials();
        m.setId(1);
        m.setBorrowableStock(5);
        when(materialsRepository.findById(1)).thenReturn(Optional.of(m));
        int avail = stockManager.getAvailableStock(1);
        assertThat(avail).isEqualTo(5);
    }

    @Test
    void getAvailableStock_missing_throws() {
        when(materialsRepository.findById(2)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> stockManager.getAvailableStock(2))
                .isInstanceOf(com.techmate.techmate.exception.NotFoundException.class);
    }

    @Test
    void reduceStock_ok_updates() {
        Materials m = new Materials();
        m.setId(3);
        m.setBorrowableStock(10);
        when(materialsRepository.findById(3)).thenReturn(Optional.of(m));
        stockManager.reduceStock(3, 4);
        verify(materialsRepository).save(m);
        assertThat(m.getBorrowableStock()).isEqualTo(6);
    }

    @Test
    void reduceStock_insufficient_throws() {
        Materials m = new Materials();
        m.setId(4);
        m.setBorrowableStock(1);
        when(materialsRepository.findById(4)).thenReturn(Optional.of(m));
        assertThatThrownBy(() -> stockManager.reduceStock(4, 2))
                .isInstanceOf(com.techmate.techmate.exception.InsufficientStockException.class);
    }

    @Test
    void restoreStock_increases_and_saves() {
        Materials m = new Materials();
        m.setId(5);
        m.setBorrowableStock(2);
        when(materialsRepository.findById(5)).thenReturn(Optional.of(m));
        stockManager.restoreStock(5, 3);
        verify(materialsRepository).save(m);
        assertThat(m.getBorrowableStock()).isEqualTo(5);
    }
}
