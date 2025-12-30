package com.techmate.techmate.infrastructure.dto.response;

/**
 * DTO para top materiales más movidos.
 * Usado en reportes y dashboards.
 */
public record TopMaterialMovementDTO(
    String materialName,       // Nombre del material
    String moveType,           // Tipo de movimiento
    Long totalQuantity,        // Cantidad total movida
    Long movementCount         // Número de movimientos
) {
    /**
     * Constructor desde array de Object[] (resultado de query nativa).
     * @param row Array con: [materialName, moveType, totalQuantity, movementCount]
     */
    public static TopMaterialMovementDTO fromArray(Object[] row) {
        return new TopMaterialMovementDTO(
            (String) row[0],
            (String) row[1],
            ((Number) row[2]).longValue(),
            ((Number) row[3]).longValue()
        );
    }
}








