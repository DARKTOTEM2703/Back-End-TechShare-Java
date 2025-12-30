package com.techmate.techmate.infrastructure.service.User.validator;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.techmate.techmate.domain.repository.RoleRepository;

@Component
public class UserValidator {

    private final RoleRepository roleRepository;

    public UserValidator(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public void validateRolesExist(Set<String> roles) {
        if (roles == null) return;
        for (String r : roles) {
            if (roleRepository.findByName(r).isEmpty()) {
                throw new IllegalArgumentException("Rol no encontrado: " + r);
            }
            
        }
    }
}









