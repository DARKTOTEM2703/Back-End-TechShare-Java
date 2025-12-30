package com.techmate.techmate.hexagonal.infrastructure.validation;

import com.techmate.techmate.repository.MaterialsRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Validador que verifica si el nombre de un material ya existe en la base de datos.
 */
public class UniqueMaterialNameValidator implements ConstraintValidator<UniqueMaterialName, String> {

    @Autowired
    private MaterialsRepository materialsRepository;

    @Override
    public boolean isValid(String name, ConstraintValidatorContext context) {
        if (name == null || name.isBlank()) {
            return true; // @NotBlank se encarga de esto
        }
        
        // Retorna true si NO existe el material con ese nombre
        return !materialsRepository.existsByName(name);
    }
}



