package com.techmate.techmate.hexagonal.ui.controller;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.techmate.techmate.config.AppProperties;
import com.techmate.techmate.hexagonal.infrastructure.dto.MaterialResponse;
import com.techmate.techmate.hexagonal.infrastructure.dto.MaterialsDTO;
import com.techmate.techmate.hexagonal.infrastructure.dto.PageResponse;
import com.techmate.techmate.hexagonal.infrastructure.service.MaterialsService;
import com.techmate.techmate.hexagonal.infrastructure.service.materials.mapper.MaterialsMapper;

/**
 * Controlador público para materiales (sin autenticación requerida)
 * Permite que cualquier usuario vea el catálogo de materiales disponibles
 */
@RestController
@RequestMapping("/api/materials")
public class PublicMaterialsController {

    private static final Logger log = LoggerFactory.getLogger(PublicMaterialsController.class);

    private final MaterialsService materialsService;
    private final MaterialsMapper materialsMapper;
    private final AppProperties appProperties;

    public PublicMaterialsController(MaterialsService materialsService, MaterialsMapper materialsMapper,
            AppProperties appProperties) {
        this.materialsService = materialsService;
        this.materialsMapper = materialsMapper;
        this.appProperties = appProperties;
    }

    /**
     * Obtiene todos los materiales con paginación (público)
     * 
     * @param page    Número de página (default 0)
     * @param size    Tamaño de página (default 10, max 100)
     * @param sortBy  Campo de ordenamiento (default: id)
     * @param sortDir Dirección de ordenamiento: asc o desc (default: asc)
     * @return Respuesta paginada con materiales
     */
    @GetMapping("/all")
    public ResponseEntity<PageResponse<MaterialResponse>> getAllMaterials(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {
        try {
            // Validar parámetros
            if (page < 0)
                page = 0;
            if (size < 1 || size > 100)
                size = 10; // Máximo 100 elementos por página

            // Crear objeto de paginación y ordenamiento
            Sort sort = sortDir.equalsIgnoreCase("desc")
                    ? Sort.by(sortBy).descending()
                    : Sort.by(sortBy).ascending();

            Pageable pageable = PageRequest.of(page, size, sort);

            // Obtener datos paginados del servicio
            Page<MaterialsDTO> materialsPage = materialsService.getAllMaterialsPaginated(pageable);

            // Mapear DTOs a respuestas
            var responseList = materialsPage.getContent().stream()
                    .map(material -> materialsMapper.toResponse(material, appProperties.getServerUrl()))
                    .collect(Collectors.toList());

            // Construir respuesta paginada
            PageResponse<MaterialResponse> pageResponse = PageResponse.<MaterialResponse>builder()
                    .content(responseList)
                    .page(materialsPage.getNumber())
                    .size(materialsPage.getSize())
                    .totalElements(materialsPage.getTotalElements())
                    .totalPages(materialsPage.getTotalPages())
                    .first(materialsPage.isFirst())
                    .last(materialsPage.isLast())
                    .hasPrevious(materialsPage.hasPrevious())
                    .hasNext(materialsPage.hasNext())
                    .build();

            return new ResponseEntity<>(pageResponse, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error("Invalid pagination parameters provided: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            log.error("Error getting paginated materials: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Obtiene materiales ordenados por precio (público)
     * 
     * @param ascending true para orden ascendente, false para descendente
     * @return Lista de materiales ordenados por precio
     */
    @GetMapping("/sorted-by-price")
    public ResponseEntity<PageResponse<MaterialResponse>> getMaterialsSortedByPrice(
            @RequestParam(value = "ascending", defaultValue = "false") boolean ascending,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        try {
            Sort.Direction direction = ascending ? Sort.Direction.ASC : Sort.Direction.DESC;
            Sort sort = Sort.by(direction, "price");
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<MaterialsDTO> materialsPage = materialsService.getAllMaterialsPaginated(pageable);

            var responseList = materialsPage.getContent().stream()
                    .map(material -> materialsMapper.toResponse(material, appProperties.getServerUrl()))
                    .collect(Collectors.toList());

            PageResponse<MaterialResponse> pageResponse = PageResponse.<MaterialResponse>builder()
                    .content(responseList)
                    .page(materialsPage.getNumber())
                    .size(materialsPage.getSize())
                    .totalElements(materialsPage.getTotalElements())
                    .totalPages(materialsPage.getTotalPages())
                    .first(materialsPage.isFirst())
                    .last(materialsPage.isLast())
                    .hasPrevious(materialsPage.hasPrevious())
                    .hasNext(materialsPage.hasNext())
                    .build();

            return new ResponseEntity<>(pageResponse, HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Error retrieving materials sorted by price: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}


