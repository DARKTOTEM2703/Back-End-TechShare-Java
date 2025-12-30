package com.techmate.techmate.hexagonal.infrastructure.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilidad para verificar roles y permisos del usuario autenticado.
 * Centraliza la lógica de validación de autorización.
 */
@Component
public class AuthorizationUtils {
    
    private static final Logger log = LoggerFactory.getLogger(AuthorizationUtils.class);
    
    /**
     * Verifica si el usuario autenticado tiene el rol ADMIN.
     * 
     * @return true si el usuario tiene rol ADMIN, false en otro caso
     */
    public static boolean isUserAdmin() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                return false;
            }
            
            // Verificar si tiene autoridad ROLE_ADMIN
            return authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(auth -> auth.equals("ROLE_ADMIN"));
        } catch (Exception e) {
            log.error("Error verificando autorización: ", e);
            return false;
        }
    }
    
    /**
     * Verifica si el usuario autenticado tiene un rol específico.
     * 
     * @param roleName nombre del rol a verificar (ej: "ADMIN", "USER")
     * @return true si el usuario tiene el rol especificado
     */
    public static boolean hasRole(String roleName) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                return false;
            }
            
            String roleToCheck = roleName.startsWith("ROLE_") ? roleName : "ROLE_" + roleName;
            
            return authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(auth -> auth.equals(roleToCheck));
        } catch (Exception e) {
            log.error("Error verificando autorización: ", e);
            return false;
        }
    }
    
    /**
     * Obtiene el nombre de usuario del usuario autenticado.
     * 
     * @return nombre de usuario o null si no está autenticado
     */
    public static String getAuthenticatedUsername() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                return null;
            }
            
            return authentication.getName();
        } catch (Exception e) {
            log.error("Error obteniendo usuario autenticado: ", e);
            return null;
        }
    }
}








