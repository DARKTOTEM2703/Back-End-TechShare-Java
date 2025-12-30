package com.techmate.techmate.hexagonal.infrastructure.service;

import com.techmate.techmate.hexagonal.infrastructure.dto.MovementsDTO;
import java.util.Date;
import java.util.List;

/**
 * ✅ Interface Segregation Principle (ISP)
 * 
 * Interfaz segregada para operaciones de LECTURA/QUERY de movimientos.
 * Responsabilidad ÚNICA: Leer y consultar movimientos con diferentes criterios
 * 
 * ✨ Beneficio: Los clientes que solo necesitan queries no son obligados
 * a implementar o mockear métodos de CRUD o token handling
 * 
 * @author TechShare Team
 * @version 1.0
 */
public interface IMovementQueryService {

    /**
     * Obtener todos los movimientos
     * @return lista de todos los movimientos
     */
    List<MovementsDTO> getAllMovementsDTO();

    /**
     * Obtener movimientos filtrados por tipo
     * @param type tipo de movimiento (ENTRADA, SALIDA, DEVOLUCION, etc)
     * @return lista de movimientos del tipo especificado
     */
    List<MovementsDTO> getMovementsByType(String type);

    /**
     * Obtener movimientos dentro de un rango de fechas
     * @param startDate fecha inicial
     * @param endDate fecha final
     * @return lista de movimientos en el rango de fechas
     */
    List<MovementsDTO> getMovementsByDate(Date startDate, Date endDate);

    /**
     * Obtener movimientos con paginación
     * @param pageNumber número de página
     * @param pageSize cantidad de registros por página
     * @return lista de movimientos paginados
     */
    List<MovementsDTO> getMovementsPaged(Integer pageNumber, Integer pageSize);

}



