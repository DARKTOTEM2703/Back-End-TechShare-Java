package com.techmate.techmate.hexagonal.infrastructure.service;

import org.springframework.stereotype.Service;

/**
 * ✅ Interface Segregation Principle (ISP)
 * 
 * Interfaz consolidada que agrupa las interfaces segregadas.
 * Esto mantiene compatibilidad hacia atrás mientras permite que los clientes
 * dependan solo de las interfaces específicas que necesitan.
 * 
 * Clientes que necesitan:
 * - Solo CRUD → Implementan IMovementCrudService
 * - Solo Queries → Implementan IMovementQueryService
 * - Solo Token → Implementan ITokenService
 * - Todo (excepto Token) → Implementan MovementsService (como ahora)
 * 
 * ✨ REFACTOR: Se removió ITokenService de MovementsService
 * Razón: ISP Principle - Los clientes de movimientos NO deben depender de token operations
 * 
 * @author TechShare Team
 * @version 2.0 (ISP Refactored - TokenService separated)
 */
@Service
public interface MovementsService extends IMovementCrudService, IMovementQueryService {

    // Interfaz consolidada que combina solo las operaciones de movimientos
    // Token operations están en TokenService (segregadas correctamente)

}




