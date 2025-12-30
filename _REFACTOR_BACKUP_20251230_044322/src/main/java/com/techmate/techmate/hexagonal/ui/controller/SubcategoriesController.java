package com.techmate.techmate.hexagonal.ui.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import com.techmate.techmate.hexagonal.infrastructure.config.AppProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.access.prepost.PreAuthorize;

// ImageStorageStrategy not needed in controller after refactor
import com.techmate.techmate.hexagonal.infrastructure.dto.SubCategoriesDTO;
import com.techmate.techmate.hexagonal.infrastructure.dto.SubCategoryRequest;
import com.techmate.techmate.hexagonal.infrastructure.dto.SubCategoryResponse;
import com.techmate.techmate.hexagonal.infrastructure.service.SubCategoriesService;
import com.techmate.techmate.hexagonal.infrastructure.service.subcategories.mapper.SubCategoriesMapper;

// CORS configurado globalmente en WebSecurityConfig - no necesita @CrossOrigin aquí
@RestController
@RequestMapping("admin/subcategories")
@PreAuthorize("hasRole('ADMIN')")
public class SubcategoriesController {

    private final SubCategoriesService subcategoriesService;

    private final SubCategoriesMapper subCategoriesMapper;

    private final AppProperties appProperties;

    public SubcategoriesController(SubCategoriesService subcategoriesService, SubCategoriesMapper subCategoriesMapper,
            AppProperties appProperties) {
        this.subcategoriesService = subcategoriesService;
        this.subCategoriesMapper = subCategoriesMapper;
        this.appProperties = appProperties;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createSubcategory(
            @Valid @ModelAttribute SubCategoryRequest request,
            @RequestParam("image") MultipartFile image) {

        SubCategoriesDTO dto = subCategoriesMapper.fromRequest(request);
        SubCategoriesDTO saved = subcategoriesService.createSubCategory(dto, image);
        SubCategoryResponse resp = subCategoriesMapper.toResponse(saved, appProperties.getServerUrl());
        return new ResponseEntity<>(resp, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSubcategoryById(@PathVariable("id") Integer id) {

        SubCategoriesDTO subcategory = subcategoriesService.getSubCategoryById(id);

        if (subcategory == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        SubCategoryResponse resp = subCategoriesMapper.toResponse(subcategory, appProperties.getServerUrl());
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateSubcategory(
            @PathVariable("id") Integer id,
            @Valid @ModelAttribute SubCategoryRequest request,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        SubCategoriesDTO dto = subCategoriesMapper.fromRequest(request);
        SubCategoriesDTO updated = subcategoriesService.updateSubCategory(id, dto, image);

        if (updated == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        SubCategoryResponse resp = subCategoriesMapper.toResponse(updated, appProperties.getServerUrl());
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllSubcategories() {
        try {
            List<SubCategoryResponse> subcategories = subcategoriesService.getAllSubCategories().stream()
                    .map(subcategory -> subCategoriesMapper.toResponse(subcategory, appProperties.getServerUrl()))
                    .collect(Collectors.toList());

            if (subcategories == null || subcategories.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT); // No hay subcategorías
            }

            return new ResponseEntity<>(subcategories, HttpStatus.OK);
        } catch (Exception e) {
            // Manejo de errores inesperados
            return new ResponseEntity<>("Error al obtener las subcategorías: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteSubcategory(@PathVariable("id") Integer id) {

        subcategoriesService.deleteSubCategory(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Eliminación exitosa
    }

    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<byte[]> getImage(@PathVariable String filename) throws IOException {

        String storageLocation = appProperties.getStorage().getLocation();
        Path imagePath = Paths.get(storageLocation).resolve(filename);
        File file = imagePath.toFile();

        if (!file.exists()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Imagen no encontrada
        }

        byte[] imageBytes = Files.readAllBytes(imagePath);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(imagePath))
                .body(imageBytes);
    }
}







