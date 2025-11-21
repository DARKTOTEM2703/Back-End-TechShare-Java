package com.techmate.techmate.service.materials.validator;

import org.springframework.stereotype.Component;

import java.util.List;

import com.techmate.techmate.exception.NotFoundException;
import com.techmate.techmate.exception.ValidationException;
import com.techmate.techmate.repository.MaterialsRepository;
import com.techmate.techmate.repository.RoleRepository;
import com.techmate.techmate.repository.SubCategoriesRepository;

/**
 * Validador de reglas de negocio para Materials (SRP).
 */
@Component
public class MaterialsValidator {

    private final MaterialsRepository materialsRepository;
    private final RoleRepository roleRepository;
    private final SubCategoriesRepository subCategoriesRepository;

    public MaterialsValidator(MaterialsRepository materialsRepository,
                              RoleRepository roleRepository,
                              SubCategoriesRepository subCategoriesRepository) {
        this.materialsRepository = materialsRepository;
        this.roleRepository = roleRepository;
        this.subCategoriesRepository = subCategoriesRepository;
    }

    public void validateUniqueName(String name) {
        if (name != null && materialsRepository.findByName(name) != null) {
            throw new ValidationException("Ya existe un material con el nombre: " + name);
        }
    }

    public void validateRolesExist(List<Integer> roleIds) {
        if (roleIds == null) return;
        for (Integer id : roleIds) {
            if (!roleRepository.findById(id).isPresent()) {
                throw new NotFoundException("Rol no encontrado con ID: " + id);
            }
        }
    }

    public void validateSubCategoryExists(Integer subCategoryId) {
        if (subCategoryId == null) {
            throw new IllegalArgumentException("SubCategory ID requerido");
        }
        if (!subCategoriesRepository.findById(subCategoryId).isPresent()) {
            throw new NotFoundException("Subcategoría no encontrada con ID: " + subCategoryId);
        }
    }
}


