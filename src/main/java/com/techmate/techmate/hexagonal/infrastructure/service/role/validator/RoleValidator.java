package com.techmate.techmate.hexagonal.infrastructure.service.role.validator;

import org.springframework.stereotype.Component;

import com.techmate.techmate.hexagonal.domain.repository.RoleRepository;

@Component
public class RoleValidator {

    private final RoleRepository roleRepository;

    public RoleValidator(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public void validateUniqueName(String name) {
        if (name != null && roleRepository.findByName(name).isPresent()) {
            throw new RuntimeException("Ya existe un rol con el nombre: " + name);
        }
    }
}








