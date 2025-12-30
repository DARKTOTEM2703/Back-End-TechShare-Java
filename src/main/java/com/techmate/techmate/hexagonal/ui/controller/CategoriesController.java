package com.techmate.techmate.hexagonal.ui.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.techmate.techmate.config.AppProperties;
import com.techmate.techmate.hexagonal.infrastructure.dto.CategoriesDTO;
import com.techmate.techmate.hexagonal.infrastructure.dto.CategoryRequest;
import com.techmate.techmate.hexagonal.infrastructure.dto.CategoryResponse;
import com.techmate.techmate.hexagonal.infrastructure.dto.ErrorResponse;
import com.techmate.techmate.hexagonal.infrastructure.imageStorage.ImageStorageStrategy;
import com.techmate.techmate.hexagonal.infrastructure.service.CategoriesService;
import com.techmate.techmate.hexagonal.infrastructure.service.categories.mapper.CategoriesMapper;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * La clase {@code CategoriesController} maneja las solicitudes HTTP
 * relacionadas
 * con las categorías en el sistema.
 */
// CORS configurado globalmente en WebSecurityConfig - no necesita @CrossOrigin
// aquí
@RestController
@RequestMapping("admin/categories")
@Validated
@PreAuthorize("hasRole('ADMIN')")
public class CategoriesController {

    private final CategoriesService categoriesService;
    private final ImageStorageStrategy imageStorageStrategy;
    private final CategoriesMapper categoriesMapper;
    private final AppProperties appProperties;

    public CategoriesController(CategoriesService categoriesService,
            ImageStorageStrategy imageStorageStrategy,
            CategoriesMapper categoriesMapper,
            AppProperties appProperties) {
        this.categoriesService = categoriesService;
        this.imageStorageStrategy = imageStorageStrategy;
        this.categoriesMapper = categoriesMapper;
        this.appProperties = appProperties;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createCategory(
            @Valid @ModelAttribute CategoryRequest categoriesRequest,
            @RequestParam("image") MultipartFile image,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            // Filtrar solo los errores relevantes
            List<String> errorMessages = bindingResult.getFieldErrors()
                    .stream()
                    .filter(error -> "name".equals(error.getField()) || "imagePath".equals(error.getField())) // Filtra
                                                                                                              // los
                                                                                                              // campos
                                                                                                              // específicos
                    .map(error -> error.getDefaultMessage()) // Obtener el mensaje de error
                    .collect(Collectors.toList());

            return new ResponseEntity<>(new ErrorResponse(errorMessages), HttpStatus.BAD_REQUEST); // Retorna errores de
                                                                                                   // validación
        }

        // Mapear request -> internal DTO usando mapper
        CategoriesDTO dto = categoriesMapper.fromRequest(categoriesRequest);
        CategoriesDTO savedCategory = categoriesService.createCategory(dto, image);

        // Mapear a response público usando mapper
        CategoryResponse response = categoriesMapper.toResponse(savedCategory, appProperties.getServerUrl());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable("id") Integer id) {

        CategoriesDTO category = categoriesService.getCategoryById(id);
        if (category == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        CategoryResponse resp = categoriesMapper.toResponse(category, appProperties.getServerUrl());

        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    // Endpoint para actualizar una categoría

    // Endpoint para actualizar una categoría
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateCategory(
            @PathVariable("id") Integer id,
            @Valid @ModelAttribute CategoryRequest categoriesRequest,
            @RequestParam(value = "image", required = false) MultipartFile image,
            BindingResult bindingResult) { // Agregamos BindingResult

        if (bindingResult.hasErrors()) {
            // Filtrar solo los errores relevantes
            List<String> errorMessages = bindingResult.getFieldErrors()
                    .stream()
                    .filter(error -> "name".equals(error.getField()) || "imagePath".equals(error.getField())) // Filtra
                                                                                                              // los
                                                                                                              // campos
                                                                                                              // específicos
                    .map(error -> error.getDefaultMessage()) // Obtener el mensaje de error
                    .collect(Collectors.toList());

            return new ResponseEntity<>(new ErrorResponse(errorMessages), HttpStatus.BAD_REQUEST); // Retorna errores de
                                                                                                   // validación
        }

        // Mapear request -> DTO y delegar la actualización
        CategoriesDTO dto = categoriesMapper.fromRequest(categoriesRequest);
        CategoriesDTO updatedCategory = categoriesService.updateCategory(id, dto, image);

        if (updatedCategory == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        CategoryResponse resp = categoriesMapper.toResponse(updatedCategory, appProperties.getServerUrl());
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {

        List<CategoryResponse> categories = categoriesService.getAllCategories().stream()
                .map(category -> categoriesMapper.toResponse(category, appProperties.getServerUrl()))
                .collect(Collectors.toList());

        if (categories.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // No hay subcategorías
        }
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable("id") Integer id) {

        // Delegar la eliminación al servicio directamente
        categoriesService.deleteCategory(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Categoría eliminada correctamente
    }

    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<byte[]> getImage(@PathVariable String filename) throws IOException {

        byte[] imageBytes = imageStorageStrategy.getImage(filename); // Utiliza el método getImage de la estrategia
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE,
                        Files.probeContentType(Paths.get(appProperties.getStorage().getLocation()).resolve(filename)))
                .body(imageBytes);
    }
}


