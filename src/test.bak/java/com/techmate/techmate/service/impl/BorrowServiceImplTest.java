package com.techmate.techmate.service.impl;

import com.techmate.techmate.dto.BorrowDTO;
import com.techmate.techmate.entity.Status;
import com.techmate.techmate.service.borrow.processor.BorrowStateProcessor;
import com.techmate.techmate.service.borrow.query.BorrowQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("🏦 BorrowService (Facade) - Tests Unitarios")
class BorrowServiceImplTest {

    @Mock
    private BorrowQueryService queryService;

    @Mock
    private BorrowStateProcessor stateProcessor;

    @InjectMocks
    private BorrowServiceImpl borrowService;

    private BorrowDTO testBorrowDTO;

    @BeforeEach
    void setUp() {
        testBorrowDTO = new BorrowDTO();
        testBorrowDTO.setId(1);
        testBorrowDTO.setDate(new Date());
        testBorrowDTO.setStatus(Status.PENDING);
        testBorrowDTO.setAmount(new BigDecimal("77.97"));
        testBorrowDTO.setUsuarioId(100);
        testBorrowDTO.setAdminId(200);
    }

    @Test
    @DisplayName("getAllBorrowDTO - Delega a QueryService")
    void getAllBorrowDTO_Delegates() {
        when(queryService.getAllBorrows()).thenReturn(Arrays.asList(testBorrowDTO));

        List<BorrowDTO> result = borrowService.getAllBorrowDTO();

        assertThat(result).hasSize(1);
        verify(queryService).getAllBorrows();
    }

    @Test
    @DisplayName("getBorrowByStatus - Delega a QueryService")
    void getBorrowByStatus_Delegates() {
        when(queryService.getBorrowsByStatus("PENDING")).thenReturn(Arrays.asList(testBorrowDTO));

        List<BorrowDTO> result = borrowService.getBorrowByStatus("PENDING");

        assertThat(result).hasSize(1);
        verify(queryService).getBorrowsByStatus("PENDING");
    }

    @Test
    @DisplayName("getBorrowByDate - Delega a QueryService")
    void getBorrowByDate_Delegates() {
        Date start = new Date();
        Date end = new Date();
        when(queryService.getBorrowsByDateRange(start, end)).thenReturn(Arrays.asList(testBorrowDTO));

        List<BorrowDTO> result = borrowService.getBorrowByDate(start, end);

        assertThat(result).hasSize(1);
        verify(queryService).getBorrowsByDateRange(start, end);
    }

    @Test
    @DisplayName("updateBorrowStatus - Delega a StateProcessor")
    void updateBorrowStatus_Delegates() throws Exception {
        doNothing().when(stateProcessor).processStateTransition(1, Status.BORROWED, 200);

        borrowService.updateBorrowStatus(1, Status.BORROWED, 200);

        verify(stateProcessor).processStateTransition(1, Status.BORROWED, 200);
    }
}
