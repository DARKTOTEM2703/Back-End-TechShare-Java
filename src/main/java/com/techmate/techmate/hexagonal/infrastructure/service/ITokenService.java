package com.techmate.techmate.hexagonal.infrastructure.service;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

/**
 * ✅ Interface Segregation Principle (ISP)
 * 
 * Interfaz segregada para operaciones de extracción y validación de tokens.
 * Responsabilidad ÚNICA: Decodificar, extraer datos, y validar tokens JWT
 * 
 * ✨ Beneficio: Los clientes que no necesitan token handling no son obligados
 * a implementar o mockear estos métodos. Desacoplamiento de preocupaciones.
 * 
 * @author TechShare Team
 * @version 1.0
 */
public interface ITokenService {

    /**
     * Decodificar y extraer información del token HTTP
     * @param request HttpServletRequest que contiene el token en headers
     * @throws InvalidTokenException si el token es inválido o expirado
     */
    void decodeToken(HttpServletRequest request);

    /**
     * Extraer ID del usuario desde el token JWT
     * @param token token JWT sin el prefijo "Bearer "
     * @return ID del usuario
     * @throws InvalidTokenException si el token es inválido o no contiene userId
     */
    Integer getUserIdFromToken(String token);

    /**
     * Extraer roles del usuario desde el token JWT
     * @param token token JWT sin el prefijo "Bearer "
     * @return lista de roles del usuario, o Optional.empty() si no hay roles
     * @throws InvalidTokenException si el token es inválido
     */
    Optional<List<Integer>> getRolesFromToken(String token);

    /**
     * Extraer token desde el HttpServletRequest
     * @param request HttpServletRequest que contiene el token en Authorization header
     * @return token JWT sin el prefijo "Bearer ", o null si no existe
     */
    String extractTokenFromRequest(HttpServletRequest request);

    /**
     * Validar que el token sea válido
     * @param token token JWT a validar
     * @return true si el token es válido, false en caso contrario
     */
    boolean isTokenValid(String token);

}








