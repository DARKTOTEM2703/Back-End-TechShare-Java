package com.techmate.techmate.service.movements.validator;

import static org.junit.jupiter.api.Assertions.*;

import com.techmate.techmate.dto.MovementsDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MovementValidatorTest {

    private MovementValidator movementValidator;

    @BeforeEach
    void setUp() {
        movementValidator = new MovementValidator();
    }

    // Tests for validateQuantity()
    @Test
    void validateQuantity_WithPositiveQuantity_NoException() {
        MovementsDTO dto = new MovementsDTO();
        dto.setQuantity(10);
        assertDoesNotThrow(() -> movementValidator.validateQuantity(dto));
    }

    @Test
    void validateQuantity_WithZeroQuantity_ThrowsException() {
        MovementsDTO dto = new MovementsDTO();
        dto.setQuantity(0);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> movementValidator.validateQuantity(dto)
        );
        assertTrue(exception.getMessage().contains("mayor a 0"));
    }

    @Test
    void validateQuantity_WithNegativeQuantity_ThrowsException() {
        MovementsDTO dto = new MovementsDTO();
        dto.setQuantity(-5);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> movementValidator.validateQuantity(dto)
        );
        assertTrue(exception.getMessage().contains("mayor a 0"));
    }

    @Test
    void validateQuantity_WithOneQuantity_NoException() {
        MovementsDTO dto = new MovementsDTO();
        dto.setQuantity(1);
        assertDoesNotThrow(() -> movementValidator.validateQuantity(dto));
    }

    @Test
    void validateQuantity_WithLargePositiveQuantity_NoException() {
        MovementsDTO dto = new MovementsDTO();
        dto.setQuantity(Integer.MAX_VALUE);
        assertDoesNotThrow(() -> movementValidator.validateQuantity(dto));
    }

    @Test
    void validateQuantity_WithMaxIntegerMinusOne_NoException() {
        MovementsDTO dto = new MovementsDTO();
        dto.setQuantity(Integer.MAX_VALUE - 1);
        assertDoesNotThrow(() -> movementValidator.validateQuantity(dto));
    }

    @Test
    void validateQuantity_WithSmallNegativeQuantity_ThrowsException() {
        MovementsDTO dto = new MovementsDTO();
        dto.setQuantity(-1);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> movementValidator.validateQuantity(dto)
        );
        assertNotNull(exception.getMessage());
    }

    @Test
    void validateQuantity_WithMinIntegerValue_ThrowsException() {
        MovementsDTO dto = new MovementsDTO();
        dto.setQuantity(Integer.MIN_VALUE);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> movementValidator.validateQuantity(dto)
        );
        assertTrue(exception.getMessage().contains("mayor a 0"));
    }

    @Test
    void validateQuantity_WithMultipleValidQuantities() {
        int[] validQuantities = {1, 5, 10, 100, 1000, Integer.MAX_VALUE};
        for (int qty : validQuantities) {
            MovementsDTO dto = new MovementsDTO();
            dto.setQuantity(qty);
            assertDoesNotThrow(() -> movementValidator.validateQuantity(dto));
        }
    }

    @Test
    void validateQuantity_WithMultipleInvalidQuantities() {
        int[] invalidQuantities = {-1, -100, 0, Integer.MIN_VALUE};
        for (int qty : invalidQuantities) {
            MovementsDTO dto = new MovementsDTO();
            dto.setQuantity(qty);
            assertThrows(IllegalArgumentException.class, () -> movementValidator.validateQuantity(dto));
        }
    }

    @Test
    void validateQuantity_ZeroQuantityErrorMessage() {
        MovementsDTO dto = new MovementsDTO();
        dto.setQuantity(0);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> movementValidator.validateQuantity(dto)
        );
        assertEquals("La cantidad debe ser mayor a 0", exception.getMessage());
    }

    @Test
    void validateQuantity_NegativeQuantityErrorMessage() {
        MovementsDTO dto = new MovementsDTO();
        dto.setQuantity(-10);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> movementValidator.validateQuantity(dto)
        );
        assertEquals("La cantidad debe ser mayor a 0", exception.getMessage());
    }

    @Test
    void validateQuantity_WithBoundaryValues() {
        // Test boundary: quantity = 1 (valid)
        MovementsDTO dto1 = new MovementsDTO();
        dto1.setQuantity(1);
        assertDoesNotThrow(() -> movementValidator.validateQuantity(dto1));

        // Test boundary: quantity = 0 (invalid)
        MovementsDTO dto2 = new MovementsDTO();
        dto2.setQuantity(0);
        assertThrows(IllegalArgumentException.class, () -> movementValidator.validateQuantity(dto2));

        // Test boundary: quantity = -1 (invalid)
        MovementsDTO dto3 = new MovementsDTO();
        dto3.setQuantity(-1);
        assertThrows(IllegalArgumentException.class, () -> movementValidator.validateQuantity(dto3));
    }

    @Test
    void validateQuantity_HundredQuantity_NoException() {
        MovementsDTO dto = new MovementsDTO();
        dto.setQuantity(100);
        assertDoesNotThrow(() -> movementValidator.validateQuantity(dto));
    }

    @Test
    void validateQuantity_ThousandQuantity_NoException() {
        MovementsDTO dto = new MovementsDTO();
        dto.setQuantity(1000);
        assertDoesNotThrow(() -> movementValidator.validateQuantity(dto));
    }

    @Test
    void validateQuantity_WithDTOProvidedDirectly() {
        MovementsDTO dto = new MovementsDTO();
        dto.setQuantity(50);
        assertDoesNotThrow(() -> movementValidator.validateQuantity(dto));
    }
}

