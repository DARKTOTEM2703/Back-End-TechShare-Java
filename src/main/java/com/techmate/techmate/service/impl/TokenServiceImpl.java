package com.techmate.techmate.service.impl;

import com.techmate.techmate.security.TokenUtils;
import com.techmate.techmate.service.TokenService;

import io.jsonwebtoken.Claims;

import java.util.*;

import org.springframework.stereotype.Service;

@Service
public class TokenServiceImpl implements TokenService {

    // Extraer el ID del usuario desde el token
    @Override
    public Integer getUserIdFromToken(String token) {
        Claims claims = TokenUtils.decodeToken(token);
        if (claims != null) {
            Object idClaim = claims.get("id");
            if (idClaim != null) {
                try {
                    // Manejar Integer, Long, String o Number
                    if (idClaim instanceof Integer) {
                        return (Integer) idClaim;
                    } else if (idClaim instanceof Long) {
                        return ((Long) idClaim).intValue();
                    } else if (idClaim instanceof Number) {
                        return ((Number) idClaim).intValue();
                    } else {
                        return Integer.parseInt(idClaim.toString());
                    }
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        }
        return null;
    }

    // Extraer los roles del usuario desde el token
    @Override
    public Optional<List<Integer>> getRolesFromToken(String token) {
        // Llamamos al método de TokenUtils que extrae los roles del token
        return TokenUtils.getRolesFromToken(token);
    }

    // Extraer el email del usuario desde el token
    @Override
    public String getUserEmailFromToken(String token) {
        // Decodificar el token y obtener los claims
        Claims claims = TokenUtils.decodeToken(token);
        if (claims != null) {
            // El email está guardado en el 'subject' del token
            return claims.getSubject();
        }
        return null;
    }

    @Override
    public String getUserNameFromToken(String token) {
        // Llamamos al método de TokenUtils que extrae el nombre de usuario del token
        return TokenUtils.getUserNameFromToken(token);
    }

}


