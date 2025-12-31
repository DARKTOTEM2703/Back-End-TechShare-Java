package com.techmate.techmate.infrastructure.service.borrow.manager;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.techmate.techmate.domain.entity.Borrow;
import com.techmate.techmate.domain.entity.Materials;
import com.techmate.techmate.domain.entity.Movements;
import com.techmate.techmate.domain.entity.Usuario;
import com.techmate.techmate.domain.repository.MaterialsRepository;
import com.techmate.techmate.domain.repository.MovementsRepository;
import com.techmate.techmate.infrastructure.exception.BorrowBusinessException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class BorrowStockManagerTest {

    @Mock
    MaterialsRepository materialsRepository;

    @Mock
    MovementsRepository movementsRepository;

    @InjectMocks
    BorrowStockManager manager;

    private Materials material;
    private Borrow borrow;
    private Usuario user;

    @BeforeEach
    void setUp() {
        material = new Materials();
        material.setMaterialsId(1);
        material.setBorrowableStock(10);
        material.setPrice(BigDecimal.ZERO);
        material.setName("TestMaterial");
        material.setDescription("desc");

        borrow = new Borrow();
        borrow.setId(100);

        user = new Usuario();
        user.setId(50);
    }

    @Test
    void reserveStock_reducesStock_and_savesMovement() {
        manager.reserveStockAndLogMovement(material, 2, borrow, user);

        assertThat(material.getBorrowableStock()).isEqualTo(8);
        verify(movementsRepository).save(any(Movements.class));
    }

    @Test
    void reserveStock_fails_when_insufficient() {
        material.setBorrowableStock(1);

        assertThatThrownBy(() -> manager.reserveStockAndLogMovement(material, 2, borrow, user))
                .isInstanceOf(BorrowBusinessException.class);
    }

    @Test
    void reserveStock_propagatesRepositoryError_and_stock_is_updated_in_memory() {
        doThrow(new RuntimeException("db error")).when(movementsRepository).save(any(Movements.class));

        assertThatThrownBy(() -> manager.reserveStockAndLogMovement(material, 2, borrow, user))
                .isInstanceOf(RuntimeException.class);

        // stock is changed in the object before save is attempted
        assertThat(material.getBorrowableStock()).isEqualTo(8);
    }
}
