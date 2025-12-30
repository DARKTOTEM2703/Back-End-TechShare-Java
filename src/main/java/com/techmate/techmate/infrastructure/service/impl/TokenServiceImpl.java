package com.techmate.techmate.infrastructure.service.impl;

import com.techmate.techmate.infrastructure.security.TokenUtils;
import com.techmate.techmate.infrastructure.service.TokenService;

import io.jsonwebtoken.Claims;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TokenServiceImpl implements TokenService {

    private static final Logger logger = LoggerFactory.getLogger(TokenServiceImpl.class);

    /**
     * Convierte un Object a Integer de manera segura.
     * Maneja Integer, Long, Number y String.
     */
    private Integer parseIntegerFromObject(Object value) {
        if (value == null) {
            return null;
        }

        try {
            if (value instanceof Integer) {
                return (Integer) value;
            } else if (value instanceof Long) {
                return ((Long) value).intValue();
            } else if (value instanceof Number) {
                return ((Number) value).intValue();
            } else {
                return Integer.parseInt(value.toString());
            }
        } catch (NumberFormatException e) {
            logger.warn("No se pudo convertir el valor a Integer: {}", value, e);
            return null;
        }
    }

    // Extraer el ID del usuario desde el token
    @Override
    public Integer getUserIdFromToken(String token) {
        try {
            Claims claims = TokenUtils.decodeToken(token);
            if (claims == null) {
                logger.debug("Claims nulas al decodificar el token");
                return null;
            }

            Object idClaim = claims.get("id");
            Integer userId = parseIntegerFromObject(idClaim);

            if (userId != null) {
                logger.debug("Usuario ID extraído del token: {}", userId);
            } else {
                logger.warn("No se pudo extraer el ID del usuario del token");
            }

            return userId;
        } catch (Exception e) {
            logger.error("Error al extraer el ID del usuario del token", e);
            return null;
        }
    }

    // Extraer los roles del usuario desde el token
    @Override
    public Optional<List<Integer>> getRolesFromToken(String token) {
        try {
            Optional<List<Integer>> roles = TokenUtils.getRolesFromToken(token);
            if (roles.isPresent()) {
                logger.debug("Roles extraídos del token: {}", roles.get());
            }
            return roles;
        } catch (Exception e) {
            logger.error("Error al extraer los roles del token", e);
            return Optional.empty();
        }
    }

    // Extraer el email del usuario desde el token
    @Override
    public String getUserEmailFromToken(String token) {
        try {
            Claims claims = TokenUtils.decodeToken(token);
            if (claims == null) {
                logger.debug("Claims nulas al decodificar el token");
                return null;
            }

            String email = claims.getSubject();
            if (email != null) {
                logger.debug("Email extraído del token: {}", email);
            }
            return email;
        } catch (Exception e) {
            logger.error("Error al extraer el email del token", e);
            return null;
        }
    }

    @Override
    public String getUserNameFromToken(String token) {
        try {
            String userName = TokenUtils.getUserNameFromToken(token);
            if (userName != null) {
                logger.debug("Nombre de usuario extraído del token: {}", userName);
            }
            return userName;
        } catch (Exception e) {
            logger.error("Error al extraer el nombre de usuario del token", e);
            return null;
        }
    }

}







