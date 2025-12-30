package com.techmate.techmate.service.borrow.validator;

import static org.junit.jupiter.api.Assertions.*;

import com.techmate.techmate.hexagonal.domain.entity.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BorrowStateValidatorTest {

    private BorrowStateValidator borrowStateValidator;

    @BeforeEach
    void setUp() {
        borrowStateValidator = new BorrowStateValidator();
    }

    // Tests for status transitions
    @Test
    void validateStateTransition_FromPendingToBorrowed_Valid() {
        assertTrue(borrowStateValidator.validateStateTransition(Status.PENDING, Status.BORROWED));
    }

    @Test
    void validateStateTransition_FromPendingToRejected_Valid() {
        assertTrue(borrowStateValidator.validateStateTransition(Status.PENDING, Status.REJECTED));
    }

    @Test
    void validateStateTransition_FromBorrowedToReturned_Valid() {
        assertTrue(borrowStateValidator.validateStateTransition(Status.BORROWED, Status.RETURNED));
    }

    @Test
    void validateStateTransition_FromReturnedToAnything_Invalid() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> borrowStateValidator.validateStateTransition(Status.RETURNED, Status.BORROWED));
        assertTrue(exception.getMessage().contains("No se puede cambiar"));
    }

    @Test
    void validateStateTransition_FromRejectedToAnything_Invalid() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> borrowStateValidator.validateStateTransition(Status.REJECTED, Status.BORROWED));
        assertTrue(exception.getMessage().contains("No se puede cambiar"));
    }

    @Test
    void validateStateTransition_SameStatusFromAndTo_Valid() {
        // Same status transition returns true (no-op)
        assertTrue(borrowStateValidator.validateStateTransition(Status.BORROWED, Status.BORROWED));
    }

    @Test
    void validateStateTransition_FromPendingToReturned_Invalid() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> borrowStateValidator.validateStateTransition(Status.PENDING, Status.RETURNED));
        assertNotNull(exception.getMessage());
    }

    @Test
    void validateStateTransition_MultipleValidTransitions() {
        assertTrue(borrowStateValidator.validateStateTransition(Status.PENDING, Status.BORROWED));
        assertTrue(borrowStateValidator.validateStateTransition(Status.BORROWED, Status.RETURNED));
    }

    @Test
    void validateStateTransition_ValidTransitionPath_Sequential() {
        // Borrow lifecycle: PENDING -> BORROWED -> RETURNED
        assertTrue(borrowStateValidator.validateStateTransition(Status.PENDING, Status.BORROWED));
        assertTrue(borrowStateValidator.validateStateTransition(Status.BORROWED, Status.RETURNED));
    }

    @Test
    void validateStateTransition_InvalidTransitionPath_SkippingState() {
        // Cannot go directly from PENDING to RETURNED
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> borrowStateValidator.validateStateTransition(Status.PENDING, Status.RETURNED));
        assertNotNull(exception.getMessage());
    }

    @Test
    void validateStateTransition_FromBorrowedToRejected_Invalid() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> borrowStateValidator.validateStateTransition(Status.BORROWED, Status.REJECTED));
        assertNotNull(exception.getMessage());
    }

    @Test
    void validateStateTransition_MultipleInvalidTransitions() {
        assertThrows(IllegalArgumentException.class,
                () -> borrowStateValidator.validateStateTransition(Status.RETURNED, Status.BORROWED));
        assertThrows(IllegalArgumentException.class,
                () -> borrowStateValidator.validateStateTransition(Status.REJECTED, Status.BORROWED));
        assertThrows(IllegalArgumentException.class,
                () -> borrowStateValidator.validateStateTransition(Status.PENDING, Status.RETURNED));
    }

    @Test
    void validateStateTransition_WithNullFromStatus_Invalid() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> borrowStateValidator.validateStateTransition(null, Status.BORROWED));
        assertTrue(exception.getMessage().contains("nulos"));
    }

    @Test
    void validateStateTransition_WithNullToStatus_Invalid() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> borrowStateValidator.validateStateTransition(Status.PENDING, null));
        assertTrue(exception.getMessage().contains("nulos"));
    }

    @Test
    void validateStateTransition_WithBothNullStatus_Invalid() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> borrowStateValidator.validateStateTransition(null, null));
        assertTrue(exception.getMessage().contains("nulos"));
    }

    @Test
    void validateStateTransition_AllValidTransitions() {
        // Test all valid state transitions
        assertTrue(borrowStateValidator.validateStateTransition(Status.PENDING, Status.BORROWED));
        assertTrue(borrowStateValidator.validateStateTransition(Status.PENDING, Status.REJECTED));
        assertTrue(borrowStateValidator.validateStateTransition(Status.BORROWED, Status.RETURNED));
    }

    @Test
    void validateStateTransition_AllInvalidTransitions() {
        // Terminal states cannot transition
        assertThrows(IllegalArgumentException.class,
                () -> borrowStateValidator.validateStateTransition(Status.RETURNED, Status.PENDING));
        assertThrows(IllegalArgumentException.class,
                () -> borrowStateValidator.validateStateTransition(Status.RETURNED, Status.BORROWED));
        assertThrows(IllegalArgumentException.class,
                () -> borrowStateValidator.validateStateTransition(Status.RETURNED, Status.REJECTED));

        assertThrows(IllegalArgumentException.class,
                () -> borrowStateValidator.validateStateTransition(Status.REJECTED, Status.PENDING));
        assertThrows(IllegalArgumentException.class,
                () -> borrowStateValidator.validateStateTransition(Status.REJECTED, Status.BORROWED));
        assertThrows(IllegalArgumentException.class,
                () -> borrowStateValidator.validateStateTransition(Status.REJECTED, Status.RETURNED));
    }

    @Test
    void validateStateTransition_RepeatedValidTransition() {
        // Can perform same transition multiple times in sequence
        for (int i = 0; i < 5; i++) {
            assertTrue(borrowStateValidator.validateStateTransition(Status.PENDING, Status.BORROWED));
        }
    }

    @Test
    void validateStateTransition_EdgeCaseAllStatusCombinations() {
        Status[] allStatuses = Status.values();

        for (Status from : allStatuses) {
            for (Status to : allStatuses) {
                if (from == Status.PENDING && (to == Status.BORROWED || to == Status.REJECTED)) {
                    // Valid transitions from PENDING
                    assertTrue(borrowStateValidator.validateStateTransition(from, to));
                } else if (from == Status.BORROWED && to == Status.RETURNED) {
                    // Valid transition from BORROWED
                    assertTrue(borrowStateValidator.validateStateTransition(from, to));
                } else if (from == to) {
                    // Same status is valid (no-op)
                    assertTrue(borrowStateValidator.validateStateTransition(from, to));
                } else if (from == Status.RETURNED || from == Status.REJECTED) {
                    // Terminal states cannot transition
                    assertThrows(IllegalArgumentException.class,
                            () -> borrowStateValidator.validateStateTransition(from, to));
                } else {
                    // Other invalid transitions
                    assertThrows(IllegalArgumentException.class,
                            () -> borrowStateValidator.validateStateTransition(from, to));
                }
            }
        }
    }

    @Test
    void validateStateTransition_PendingBorrowedReturnedSequence() {
        // Test the complete valid lifecycle
        assertTrue(borrowStateValidator.validateStateTransition(Status.PENDING, Status.BORROWED));
        assertTrue(borrowStateValidator.validateStateTransition(Status.BORROWED, Status.RETURNED));
    }

    @Test
    void validateStateTransition_PendingRejectedSequence() {
        // Test alternate valid lifecycle
        assertTrue(borrowStateValidator.validateStateTransition(Status.PENDING, Status.REJECTED));
    }

    @Test
    void validateStateTransition_FromPendingToRejected_ThenAttemptTransition() {
        assertTrue(borrowStateValidator.validateStateTransition(Status.PENDING, Status.REJECTED));
        // After rejection, no further transitions
        assertThrows(IllegalArgumentException.class,
                () -> borrowStateValidator.validateStateTransition(Status.REJECTED, Status.BORROWED));
    }
}
