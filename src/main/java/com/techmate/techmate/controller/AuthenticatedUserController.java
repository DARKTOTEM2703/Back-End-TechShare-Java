package com.techmate.techmate.controller;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.techmate.techmate.security.UserDetailsImpl;
import com.techmate.techmate.dto.CurrentUserDTO;

/**
 * Controlador para endpoints accesibles por cualquier usuario autenticado
 */
@RestController
@RequestMapping("/user")
public class AuthenticatedUserController {

    private static final Logger log = LoggerFactory.getLogger(AuthenticatedUserController.class);

    /**
     * Obtiene la información del usuario autenticado actualmente
     * @return datos del usuario con su nombre, email y roles
     */
    @GetMapping("/me")
    public ResponseEntity<CurrentUserDTO> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        log.debug("GET /user/me - Authentication object: {}", authentication);
        log.debug("Is authenticated: {}", authentication != null ? authentication.isAuthenticated() : "null");
        
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Unauthorized access attempt to /user/me");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado");
        }

        if (authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            
            CurrentUserDTO currentUser = new CurrentUserDTO();
            currentUser.setId(userDetails.getId());
            currentUser.setUser_name(userDetails.getNombre()); // username del usuario (jafethgamboabaas)
            currentUser.setFirst_name(userDetails.getFirstName());
            currentUser.setLast_name(userDetails.getLastName());
            currentUser.setEmail(userDetails.getUsername()); // Email es el username en Spring Security
            currentUser.setRoles(userDetails.getAuthorities().stream()
                    .map(auth -> auth.getAuthority())
                    .collect(Collectors.toList()));
            
            log.info("User info returned for: {}", userDetails.getUsername());
            return ResponseEntity.ok(currentUser);
        }
        
        // Si el principal no es la implementación esperada, puede deberse a que
        // la autenticación no se resolvió como un usuario (p.ej. token inválido
        // o anonymous). Devolver 401 para que el cliente SPA pueda manejarlo
        // (mostrar modal / redirigir al login) en lugar de un 500 internamente.
        log.warn("Principal is not UserDetailsImpl - returning 401");
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado");
    }
}


