package com.techmate.techmate.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class BorrowTest {

    @Test
    @DisplayName("Debe lanzar excepción si se intenta aprobar un préstamo que ya está cerrado")
    void shouldThrowExceptionWhenApprovingClosedBorrow() {
        // GIVEN: Un préstamo en estado BORROWED que será retornado
        Borrow borrow = new Borrow();
        borrow.setId(1);
        borrow.setStatus(com.techmate.techmate.entity.Status.BORROWED);

        // Simulamos la devolución
        borrow.changeStatus(com.techmate.techmate.entity.Status.RETURNED, 99);

        // WHEN & THEN: Intentar cambiar estado desde RETURNED a BORROWED debe fallar
        assertThrows(IllegalStateException.class,
                () -> borrow.changeStatus(com.techmate.techmate.entity.Status.BORROWED, 99));
    }

    @Test
    @DisplayName("Debe calcular correctamente el monto total sumando los detalles")
    void shouldCalculateTotalAmountCorrectly() {
        Borrow borrow = new Borrow();
        com.techmate.techmate.domain.model.DetailsBorrow d1 = new com.techmate.techmate.domain.model.DetailsBorrow();
        d1.setTotalPrice(new BigDecimal("10.00"));
        com.techmate.techmate.domain.model.DetailsBorrow d2 = new com.techmate.techmate.domain.model.DetailsBorrow();
        d2.setTotalPrice(new BigDecimal("20.00"));

        borrow.setDetails(Arrays.asList(d1, d2));

        BigDecimal total = borrow.calculateTotalAmount();

        assertEquals(new BigDecimal("30.00"), total);
    }
}
