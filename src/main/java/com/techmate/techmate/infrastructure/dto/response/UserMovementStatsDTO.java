package com.techmate.techmate.infrastructure.dto.response;

/**
 * DTO para estadísticas de movimientos por usuario.
 */
public record UserMovementStatsDTO(
    String userName,           // Nombre del usuario
    String moveType,           // Tipo de movimiento
    Long totalMovements,       // Cantidad de movimientos realizados
    Long totalQuantity         // Suma de cantidades movidas
) {
    /**
     * Constructor desde array de Object[] (resultado de query nativa).
     * @param row Array con: [userName, moveType, totalMovements, totalQuantity]
     */
    public static UserMovementStatsDTO fromArray(Object[] row) {
        return new UserMovementStatsDTO(
            (String) row[0],
            (String) row[1],
            ((Number) row[2]).longValue(),
            ((Number) row[3]).longValue()
        );
    }
}








