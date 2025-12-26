package com.techmate.techmate.application.usecase;

import com.techmate.techmate.domain.model.Borrow;
import com.techmate.techmate.domain.port.out.BorrowRepositoryPort;
import com.techmate.techmate.entity.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UpdateBorrowStatusUseCase (Onion Architecture).
 * Tests domain state transitions: PENDING -> BORROWED, BORROWED -> RETURNED, etc.
 */
@ExtendWith(MockitoExtension.class)
class UpdateBorrowStatusUseCaseTest {

    @Mock
    BorrowRepositoryPort borrowRepositoryPort;

    @InjectMocks
    UpdateBorrowStatusUseCase updateBorrowStatusUseCase;

    @Test
    void execute_withValidTransition_changesPendingToBorrowed() throws Exception {
        // Arrange
        Integer borrowId = 1;
        Borrow borrow = new Borrow();
        borrow.setId(borrowId);
        borrow.setStatus(Status.PENDING);

        when(borrowRepositoryPort.findById(borrowId)).thenReturn(Optional.of(borrow));

        // Act
        updateBorrowStatusUseCase.execute(borrowId, Status.BORROWED, 999);

        // Assert
        ArgumentCaptor<Borrow> captor = ArgumentCaptor.forClass(Borrow.class);
        verify(borrowRepositoryPort).save(captor.capture());
        Borrow saved = captor.getValue();
        assertThat(saved.getStatus()).isEqualTo(Status.BORROWED);
        assertThat(saved.getStartDate()).isNotNull();
    }

    @Test
    void execute_withValidTransition_changesBorrowedToReturned() throws Exception {
        // Arrange
        Integer borrowId = 1;
        Borrow borrow = new Borrow();
        borrow.setId(borrowId);
        borrow.setStatus(Status.BORROWED);

        when(borrowRepositoryPort.findById(borrowId)).thenReturn(Optional.of(borrow));

        // Act
        updateBorrowStatusUseCase.execute(borrowId, Status.RETURNED, 999);

        // Assert
        ArgumentCaptor<Borrow> captor = ArgumentCaptor.forClass(Borrow.class);
        verify(borrowRepositoryPort).save(captor.capture());
        Borrow saved = captor.getValue();
        assertThat(saved.getStatus()).isEqualTo(Status.RETURNED);
        assertThat(saved.getReturnDate()).isNotNull();
    }

    @Test
    void execute_withValidTransition_changePendingToRejected() throws Exception {
        // Arrange
        Integer borrowId = 1;
        Borrow borrow = new Borrow();
        borrow.setId(borrowId);
        borrow.setStatus(Status.PENDING);

        when(borrowRepositoryPort.findById(borrowId)).thenReturn(Optional.of(borrow));

        // Act
        updateBorrowStatusUseCase.execute(borrowId, Status.REJECTED, 999);

        // Assert
        ArgumentCaptor<Borrow> captor = ArgumentCaptor.forClass(Borrow.class);
        verify(borrowRepositoryPort).save(captor.capture());
        Borrow saved = captor.getValue();
        assertThat(saved.getStatus()).isEqualTo(Status.REJECTED);
    }

    @Test
    void execute_withInvalidId_throwsIllegalArgumentException() {
        // Act & Assert
        assertThatThrownBy(() -> updateBorrowStatusUseCase.execute(null, Status.BORROWED, 999))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID del préstamo inválido");

        verify(borrowRepositoryPort, never()).save(any());
    }

    @Test
    void execute_withInvalidId_zero_throwsIllegalArgumentException() {
        // Act & Assert
        assertThatThrownBy(() -> updateBorrowStatusUseCase.execute(0, Status.BORROWED, 999))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID del préstamo inválido");

        verify(borrowRepositoryPort, never()).save(any());
    }

    @Test
    void execute_borrowNotFound_throwsIllegalArgumentException() {
        // Arrange
        when(borrowRepositoryPort.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> updateBorrowStatusUseCase.execute(999, Status.BORROWED, 999))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Préstamo no encontrado");

        verify(borrowRepositoryPort, never()).save(any());
    }

    @Test
    void execute_withInvalidStateTransition_throwsException() {
        // Arrange: Try to transition from BORROWED to PENDING (invalid)
        Integer borrowId = 1;
        Borrow borrow = new Borrow();
        borrow.setId(borrowId);
        borrow.setStatus(Status.BORROWED);

        when(borrowRepositoryPort.findById(borrowId)).thenReturn(Optional.of(borrow));

        // Act & Assert
        assertThatThrownBy(() -> updateBorrowStatusUseCase.execute(borrowId, Status.PENDING, 999))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Transición de estado no permitida");

        verify(borrowRepositoryPort, never()).save(any());
    }

    @Test
    void execute_multipleTransitions_succeed() throws Exception {
        // Test the full lifecycle: PENDING -> BORROWED -> RETURNED
        Integer borrowId = 1;

        // First transition: PENDING -> BORROWED
        Borrow borrow = new Borrow();
        borrow.setId(borrowId);
        borrow.setStatus(Status.PENDING);
        when(borrowRepositoryPort.findById(borrowId)).thenReturn(Optional.of(borrow));

        updateBorrowStatusUseCase.execute(borrowId, Status.BORROWED, 999);

        ArgumentCaptor<Borrow> captor1 = ArgumentCaptor.forClass(Borrow.class);
        verify(borrowRepositoryPort).save(captor1.capture());
        assertThat(captor1.getValue().getStatus()).isEqualTo(Status.BORROWED);

        // Second transition: BORROWED -> RETURNED
        Borrow borrowAfterFirstTransition = new Borrow();
        borrowAfterFirstTransition.setId(borrowId);
        borrowAfterFirstTransition.setStatus(Status.BORROWED);
        when(borrowRepositoryPort.findById(borrowId)).thenReturn(Optional.of(borrowAfterFirstTransition));

        updateBorrowStatusUseCase.execute(borrowId, Status.RETURNED, 999);

        ArgumentCaptor<Borrow> captor2 = ArgumentCaptor.forClass(Borrow.class);
        verify(borrowRepositoryPort, times(2)).save(captor2.capture());
        assertThat(captor2.getValue().getStatus()).isEqualTo(Status.RETURNED);
    }
}
