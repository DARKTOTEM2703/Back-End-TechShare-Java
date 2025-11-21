package com.techmate.techmate.dto.response;

/**
 * DTO para estadísticas mensuales de movimientos.
 * Usado en queries de agregación nativas.
 */
public record MovementStatsDTO(
    String month,              // Formato: YYYY-MM
    String moveType,           // ENTRADA / SALIDA
    Long totalMovements,       // Cantidad de movimientos
    Long totalQuantity,        // Suma de cantidades
    Long uniqueMaterials       // Materiales únicos movidos
) {
    /**
     * Constructor desde array de Object[] (resultado de query nativa).
     * @param row Array con: [month, moveType, totalMovements, totalQuantity, uniqueMaterials]
     */
    public static MovementStatsDTO fromArray(Object[] row) {
        return new MovementStatsDTO(
            (String) row[0],
            (String) row[1],
            ((Number) row[2]).longValue(),
            ((Number) row[3]).longValue(),
            ((Number) row[4]).longValue()
        );
    }
    
    /**
     * Valida que los datos sean correctos.
     */
    public boolean isValid() {
        return month != null && 
               moveType != null && 
               totalMovements >= 0 && 
               totalQuantity >= 0 && 
               uniqueMaterials >= 0;
    }
}


