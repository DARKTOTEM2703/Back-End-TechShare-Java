package com.techmate.techmate.hexagonal.infrastructure.service.categories.validator;

import org.springframework.stereotype.Component;

import com.techmate.techmate.hexagonal.domain.repository.CategoriesRepository;

@Component
public class CategoriesValidator {

    private final CategoriesRepository categoriesRepository;

    public CategoriesValidator(CategoriesRepository categoriesRepository) {
        this.categoriesRepository = categoriesRepository;
    }

    public void validateUniqueName(String name) {
        if (name != null && categoriesRepository.findByName(name) != null) {
            throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + name);
        }
    }
}




