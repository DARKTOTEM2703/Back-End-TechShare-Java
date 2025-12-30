package com.techmate.techmate.infrastructure.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Respuesta paginada genérica para endpoints que retornan listas
 * 
 * @param <T> Tipo de contenido de la página
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    
    /**
     * Contenido de la página actual
     */
    private List<T> content;
    
    /**
     * Número de página actual (inicia en 0)
     */
    private int page;
    
    /**
     * Tamaño de la página (elementos por página)
     */
    private int size;
    
    /**
     * Total de elementos en todas las páginas
     */
    private long totalElements;
    
    /**
     * Total de páginas disponibles
     */
    private int totalPages;
    
    /**
     * Indica si es la primera página
     */
    private boolean first;
    
    /**
     * Indica si es la última página
     */
    private boolean last;
    
    /**
     * Indica si hay una página anterior
     */
    private boolean hasPrevious;
    
    /**
     * Indica si hay una página siguiente
     */
    private boolean hasNext;
}








