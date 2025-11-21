package com.techmate.techmate.service.materials.validator;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.techmate.techmate.repository.MaterialsRepository;
import com.techmate.techmate.repository.RoleRepository;
import com.techmate.techmate.repository.SubCategoriesRepository;

@ExtendWith(MockitoExtension.class)
public class MaterialsValidatorTest {

    @Mock
    MaterialsRepository materialsRepository;

    @Mock
    RoleRepository roleRepository;

    @Mock
    SubCategoriesRepository subCategoriesRepository;

    @InjectMocks
    MaterialsValidator validator;

    @Test
    void validateUniqueName_whenExists_throws() {
        when(materialsRepository.findByName("X")).thenReturn(new com.techmate.techmate.entity.Materials());
    assertThatThrownBy(() -> validator.validateUniqueName("X")).isInstanceOf(com.techmate.techmate.exception.ValidationException.class);
    }

    @Test
    void validateUniqueName_whenNotExists_ok() {
        when(materialsRepository.findByName("Y")).thenReturn(null);
        // should not throw
        validator.validateUniqueName("Y");
    }

    @Test
    void validateRolesExist_missing_throws() {
        when(roleRepository.findById(1)).thenReturn(java.util.Optional.empty());
        // now should throw NotFoundException
    assertThatThrownBy(() -> validator.validateRolesExist(java.util.Arrays.asList(1))).isInstanceOf(com.techmate.techmate.exception.NotFoundException.class);
    }

    @Test
    void validateSubCategoryExists_missing_throws() {
        when(subCategoriesRepository.findById(2)).thenReturn(java.util.Optional.empty());
        // now should throw NotFoundException
    assertThatThrownBy(() -> validator.validateSubCategoryExists(2)).isInstanceOf(com.techmate.techmate.exception.NotFoundException.class);
    }

    @Test
    void validateRolesExist_withValidRoles_ok() {
        com.techmate.techmate.entity.Role role = new com.techmate.techmate.entity.Role();
        when(roleRepository.findById(1)).thenReturn(java.util.Optional.of(role));
        when(roleRepository.findById(2)).thenReturn(java.util.Optional.of(role));
        // should not throw
        validator.validateRolesExist(java.util.Arrays.asList(1, 2));
    }

    @Test
    void validateRolesExist_withNullList_ok() {
        // should not throw
        validator.validateRolesExist(null);
    }

    @Test
    void validateRolesExist_withEmptyList_ok() {
        // should not throw
        validator.validateRolesExist(java.util.Arrays.asList());
    }

    @Test
    void validateSubCategoryExists_withValidId_ok() {
        com.techmate.techmate.entity.SubCategories subCategory = new com.techmate.techmate.entity.SubCategories();
        when(subCategoriesRepository.findById(5)).thenReturn(java.util.Optional.of(subCategory));
        // should not throw
        validator.validateSubCategoryExists(5);
    }

    @Test
    void validateSubCategoryExists_withNullId_throws() {
        assertThatThrownBy(() -> validator.validateSubCategoryExists(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void validateUniqueName_withNull_ok() {
        // should not throw - null check passes
        validator.validateUniqueName(null);
    }

    @Test
    void validateUniqueName_withEmptyString_ok() {
        when(materialsRepository.findByName("")).thenReturn(null);
        // should not throw
        validator.validateUniqueName("");
    }

    @Test
    void validateUniqueName_multipleUniqueNames_ok() {
        when(materialsRepository.findByName("Laptop")).thenReturn(null);
        when(materialsRepository.findByName("Monitor")).thenReturn(null);
        when(materialsRepository.findByName("Mouse")).thenReturn(null);
        
        validator.validateUniqueName("Laptop");
        validator.validateUniqueName("Monitor");
        validator.validateUniqueName("Mouse");
        
        verify(materialsRepository, times(3)).findByName(anyString());
    }

    @Test
    void validateRolesExist_withMultipleRoles_firstInvalid_throws() {
        when(roleRepository.findById(999)).thenReturn(java.util.Optional.empty());
        
        assertThatThrownBy(() -> validator.validateRolesExist(java.util.Arrays.asList(999, 1, 2)))
                .isInstanceOf(com.techmate.techmate.exception.NotFoundException.class);
    }

    @Test
    void validateSubCategoryExists_withNegativeId_throws() {
        when(subCategoriesRepository.findById(-1)).thenReturn(java.util.Optional.empty());
        
        assertThatThrownBy(() -> validator.validateSubCategoryExists(-1))
                .isInstanceOf(com.techmate.techmate.exception.NotFoundException.class);
    }

    @Test
    void validateSubCategoryExists_withZeroId_throws() {
        when(subCategoriesRepository.findById(0)).thenReturn(java.util.Optional.empty());
        
        assertThatThrownBy(() -> validator.validateSubCategoryExists(0))
                .isInstanceOf(com.techmate.techmate.exception.NotFoundException.class);
    }

    @Test
    void validateRolesExist_withLargeIdList_ok() {
        com.techmate.techmate.entity.Role role = new com.techmate.techmate.entity.Role();
        java.util.List<Integer> roleIds = java.util.Arrays.asList(1, 2, 3, 4, 5, 10, 50, 100);
        
        for (Integer id : roleIds) {
            when(roleRepository.findById(id)).thenReturn(java.util.Optional.of(role));
        }
        
        validator.validateRolesExist(roleIds);
        
        verify(roleRepository, times(8)).findById(anyInt());
    }
}

