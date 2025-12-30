package com.techmate.techmate.service.categories.validator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.techmate.techmate.hexagonal.domain.entity.Categories;com.techmate.techmate.hexagonal.domain.repository.CategoriesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class CategoriesValidatorTest {

    @Mock
    private CategoriesRepository categoriesRepository;

    private CategoriesValidator categoriesValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        categoriesValidator = new CategoriesValidator(categoriesRepository);
    }

    // Tests for validateUniqueName()
    @Test
    void validateUniqueName_WithUniqueNameAndNullResult_NoException() {
        when(categoriesRepository.findByName("Electronics")).thenReturn(null);
        assertDoesNotThrow(() -> categoriesValidator.validateUniqueName("Electronics"));
    }

    @Test
    void validateUniqueName_WithDuplicateName_ThrowsIllegalArgumentException() {
        Categories existingCategory = new Categories();
        when(categoriesRepository.findByName("Electronics")).thenReturn(existingCategory);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> categoriesValidator.validateUniqueName("Electronics"));
        assertTrue(exception.getMessage().contains("Ya existe una categoría"));
    }

    @Test
    void validateUniqueName_WithNullName_NoException() {
        assertDoesNotThrow(() -> categoriesValidator.validateUniqueName(null));
        verify(categoriesRepository, never()).findByName(anyString());
    }

    @Test
    void validateUniqueName_WithEmptyString_NoException() {
        when(categoriesRepository.findByName("")).thenReturn(null);
        assertDoesNotThrow(() -> categoriesValidator.validateUniqueName(""));
    }

    @Test
    void validateUniqueName_WithMultipleUniqueName_NoException() {
        when(categoriesRepository.findByName("Computers")).thenReturn(null);
        when(categoriesRepository.findByName("Accessories")).thenReturn(null);
        when(categoriesRepository.findByName("Software")).thenReturn(null);

        assertDoesNotThrow(() -> categoriesValidator.validateUniqueName("Computers"));
        assertDoesNotThrow(() -> categoriesValidator.validateUniqueName("Accessories"));
        assertDoesNotThrow(() -> categoriesValidator.validateUniqueName("Software"));

        verify(categoriesRepository, times(3)).findByName(anyString());
    }

    @Test
    void validateUniqueName_WithRepositoryCalledCorrectly() {
        when(categoriesRepository.findByName("Electronics")).thenReturn(null);
        categoriesValidator.validateUniqueName("Electronics");
        verify(categoriesRepository, times(1)).findByName("Electronics");
    }

    @Test
    void validateUniqueName_WithDuplicateNameErrorMessageContainsName() {
        Categories existingCategory = new Categories();
        when(categoriesRepository.findByName("Laptops")).thenReturn(existingCategory);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> categoriesValidator.validateUniqueName("Laptops"));
        assertTrue(exception.getMessage().contains("Laptops"));
    }

    @Test
    void validateUniqueName_WithBlankString_NoException() {
        when(categoriesRepository.findByName("   ")).thenReturn(null);
        assertDoesNotThrow(() -> categoriesValidator.validateUniqueName("   "));
    }

    @Test
    void validateUniqueName_WithSpecialCharacters_NoException() {
        when(categoriesRepository.findByName("Devices & Accessories")).thenReturn(null);
        assertDoesNotThrow(() -> categoriesValidator.validateUniqueName("Devices & Accessories"));
    }

    @Test
    void validateUniqueName_WithNumbers_NoException() {
        when(categoriesRepository.findByName("Category123")).thenReturn(null);
        assertDoesNotThrow(() -> categoriesValidator.validateUniqueName("Category123"));
    }

    @Test
    void validateUniqueName_WithLongName_NoException() {
        String longName = "A".repeat(200);
        when(categoriesRepository.findByName(longName)).thenReturn(null);
        assertDoesNotThrow(() -> categoriesValidator.validateUniqueName(longName));
    }

    @Test
    void validateUniqueName_WithUnicodeCharacters_NoException() {
        when(categoriesRepository.findByName("Categoría Español")).thenReturn(null);
        assertDoesNotThrow(() -> categoriesValidator.validateUniqueName("Categoría Español"));
    }

    @Test
    void validateUniqueName_RepositoryNeverCalledWithNull() {
        categoriesValidator.validateUniqueName(null);
        verify(categoriesRepository, never()).findByName(null);
    }

    @Test
    void validateUniqueName_MultipleCallsWithSameName() {
        when(categoriesRepository.findByName("Hardware")).thenReturn(null);

        for (int i = 0; i < 5; i++) {
            assertDoesNotThrow(() -> categoriesValidator.validateUniqueName("Hardware"));
        }

        verify(categoriesRepository, times(5)).findByName("Hardware");
    }

    @Test
    void validateUniqueName_DuplicateNameThrowsCorrectMessage() {
        Categories existing = new Categories();
        when(categoriesRepository.findByName("DuplicateName")).thenReturn(existing);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> categoriesValidator.validateUniqueName("DuplicateName"));

        assertEquals("Ya existe una categoría con el nombre: DuplicateName", exception.getMessage());
    }

    @Test
    void validateUniqueName_FirstCallThrowsThenSecondCallSucceeds() {
        when(categoriesRepository.findByName("Category1")).thenReturn(new Categories());
        when(categoriesRepository.findByName("Category2")).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> categoriesValidator.validateUniqueName("Category1"));
        assertDoesNotThrow(() -> categoriesValidator.validateUniqueName("Category2"));
    }

    @Test
    void validateUniqueName_EdgeCaseWhitespaceHandling() {
        when(categoriesRepository.findByName("\t")).thenReturn(null);
        when(categoriesRepository.findByName("\n")).thenReturn(null);

        assertDoesNotThrow(() -> categoriesValidator.validateUniqueName("\t"));
        assertDoesNotThrow(() -> categoriesValidator.validateUniqueName("\n"));
    }

    @Test
    void validateUniqueName_CaseInsensitivity() {
        Categories existing = new Categories();
        when(categoriesRepository.findByName("ELECTRONICS")).thenReturn(existing);

        // This test assumes repository is case-insensitive
        // If it returns existing category for "ELECTRONICS", the validator throws
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> categoriesValidator.validateUniqueName("ELECTRONICS"));
        assertNotNull(exception.getMessage());
    }
}
