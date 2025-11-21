package com.techmate.techmate.controller;

import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techmate.techmate.config.AppProperties;
import com.techmate.techmate.service.materials.mapper.MaterialsMapper;
import com.techmate.techmate.service.MaterialsService;
import com.techmate.techmate.service.EmailService;
import com.techmate.techmate.dto.MaterialRequest;
import com.techmate.techmate.dto.MaterialResponse;
import com.techmate.techmate.dto.MaterialsDTO;
import com.techmate.techmate.dto.PageResponse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.access.prepost.PreAuthorize;

// CORS configurado globalmente en WebSecurityConfig - no necesita @CrossOrigin aquí
@RestController
@RequestMapping("/admin/materials")
@PreAuthorize("hasRole('ADMIN')")
public class MaterialsController {

    private static final Logger log = LoggerFactory.getLogger(MaterialsController.class);

    private final MaterialsService materialsService;
    private final EmailService emailService;
    private final MaterialsMapper materialsMapper;
    private final AppProperties appProperties;

    public MaterialsController(MaterialsService materialsService, EmailService emailService,
            MaterialsMapper materialsMapper, AppProperties appProperties) {
        this.materialsService = materialsService;
        this.emailService = emailService;
        this.materialsMapper = materialsMapper;
        this.appProperties = appProperties;
    }

    @PostMapping("/create")
    public ResponseEntity<MaterialResponse> createMaterials(
            @RequestParam("image") MultipartFile image,
            @ModelAttribute MaterialRequest materialRequest) {

        try {
        // Mapear request público -> DTO interno usando MaterialsMapper
        MaterialsDTO requestDto = materialsMapper.fromRequest(materialRequest);
        requestDto.setImagePath(image != null ? image.getOriginalFilename() : null);

        MaterialsDTO createdMaterial = materialsService.createMaterials(requestDto, image);

        // Mapear DTO interno -> response público
        MaterialResponse resp = materialsMapper.toResponse(createdMaterial, appProperties.getServerUrl());

            return new ResponseEntity<>(resp, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            log.error("Invalid material data: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            log.error("Runtime error creating material: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/{id}")
    public ResponseEntity<MaterialResponse> getMaterialById(@PathVariable("id") Integer id) {
        try {
            MaterialsDTO materialsDTO = materialsService.getMaterialsById(id);
            MaterialResponse resp = materialsMapper.toResponse(materialsDTO, appProperties.getServerUrl());
            return new ResponseEntity<>(resp, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.debug("Material not found with id: {}", id, e);
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            log.error("Unexpected error retrieving material with id {}: {}", id, e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateMaterials(
            @PathVariable("id") Integer id,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @ModelAttribute MaterialRequest materialRequest) {

        MaterialsDTO materialsDTO = materialsMapper.fromRequest(materialRequest);
        // image handled by controller when provided
        if (image != null) materialsDTO.setImagePath(image.getOriginalFilename());

        MaterialsDTO updatedMaterial = materialsService.updateMaterials(id, materialsDTO, image);

        if (updatedMaterial == null) {
            return new ResponseEntity<>("Error al actualizar un nuevo material", HttpStatus.NOT_FOUND);
        }

        MaterialResponse resp = materialsMapper.toResponse(updatedMaterial, appProperties.getServerUrl());

        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    /**
     * Obtiene todos los materiales con paginación y ordenamiento
     * 
     * @param page Número de página (default 0)
     * @param size Tamaño de página (default 10, max 100)
     * @param sortBy Campo de ordenamiento (default: id)
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
            if (page < 0) page = 0;
            if (size < 1 || size > 100) size = 10; // Máximo 100 elementos por página
            
            // Crear objeto de paginación y ordenamiento
            Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
            
            Pageable pageable = PageRequest.of(page, size, sort);
            
            // Obtener datos paginados del servicio
            Page<MaterialsDTO> materialsPage = materialsService.getAllMaterialsPaginated(pageable);
            
            // Mapear DTOs a respuestas
            List<MaterialResponse> responseList = materialsPage.getContent().stream()
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

    @GetMapping("/sorted-by-price")
    public ResponseEntity<List<MaterialResponse>> getAllMaterialsSortedByPrice(
            @RequestParam(value = "asc", defaultValue = "false") boolean ascending) {
        try {
            List<MaterialsDTO> materialsDTO = materialsService.getAllMaterialsSortedByPrice(ascending);

            if (materialsDTO.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            List<MaterialResponse> resp = materialsDTO.stream()
                    .map(material -> materialsMapper.toResponse(material, appProperties.getServerUrl()))
                    .collect(Collectors.toList());

            return new ResponseEntity<>(resp, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error("Invalid sort parameters: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            log.error("Error retrieving materials sorted by price: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteMaterials(@PathVariable("id") Integer id) {
        try {
            MaterialsDTO material = materialsService.getMaterialsById(id); // Verifica si el material existe

            if (material == null) {
                return new ResponseEntity<>("Material no encontrado", HttpStatus.NOT_FOUND);
            }

            materialsService.deleteMaterials(id);
            return new ResponseEntity<>("Material eliminado con éxito", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/test-email")
    public ResponseEntity<String> testEmail(@RequestParam(value = "email", required = false) String email) {
        try {
            String targetEmail = email != null ? email : "rodrigorafaelchipacheco@gmail.com";
            emailService.sendEmail(targetEmail, "Test", "Este es un mensaje de prueba.");
            return new ResponseEntity<>("Correo enviado a: " + targetEmail, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error("Invalid email parameter: {}", e.getMessage());
            return new ResponseEntity<>("Error enviando correo: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            log.error("Error enviando correo: {}", e.getMessage(), e);
            return new ResponseEntity<>("Error enviando correo: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<byte[]> getImage(@PathVariable String filename) throws IOException {
        Path imagePath = Paths.get(appProperties.getStorage().getLocation()).resolve(filename);
        File file = imagePath.toFile();

        if (!file.exists()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        byte[] imageBytes = Files.readAllBytes(imagePath);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(imagePath))
                .body(imageBytes);
    }
}


