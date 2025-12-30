package com.techmate.techmate.hexagonal.ui.controller;

import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techmate.techmate.hexagonal.application.port.input.MaterialManagementUseCase;
import com.techmate.techmate.hexagonal.infrastructure.config.AppProperties;
import com.techmate.techmate.hexagonal.infrastructure.service.EmailService;
import com.techmate.techmate.hexagonal.infrastructure.dto.MaterialRequest;
import com.techmate.techmate.hexagonal.infrastructure.dto.MaterialResponse;
import com.techmate.techmate.hexagonal.infrastructure.dto.PageResponse;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * MaterialsController - Hexagonal Architecture
 * Adapter (UI Layer) que usa MaterialManagementUseCase (puerto de entrada)
 * NO usa MaterialsService legacy. Solo puertos.
 */
@RestController
@RequestMapping("/api/materials")
@PreAuthorize("hasRole('ADMIN')")
public class MaterialsController {

    private static final Logger log = LoggerFactory.getLogger(MaterialsController.class);

    private final MaterialManagementUseCase materialManagementUseCase;
    private final EmailService emailService;
    private final AppProperties appProperties;

    public MaterialsController(MaterialManagementUseCase materialManagementUseCase, 
            EmailService emailService,
            AppProperties appProperties) {
        this.materialManagementUseCase = materialManagementUseCase;
        this.emailService = emailService;
        this.appProperties = appProperties;
    }


    @PostMapping("/create")
    public ResponseEntity<MaterialResponse> createMaterial(
            @RequestParam("image") MultipartFile image,
            @ModelAttribute MaterialRequest materialRequest) {
        try {
            MaterialResponse resp = materialManagementUseCase.createMaterial(materialRequest);
            return new ResponseEntity<>(resp, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            log.error("Invalid material data: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            log.error("Error creating material: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaterialResponse> getMaterialById(@PathVariable("id") Long id) {
        try {
            MaterialResponse resp = materialManagementUseCase.getMaterialById(id);
            return new ResponseEntity<>(resp, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.debug("Material not found with id: {}", id, e);
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            log.error("Error retrieving material with id {}: {}", id, e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MaterialResponse> updateMaterial(
            @PathVariable("id") Long id,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @ModelAttribute MaterialRequest materialRequest) {
        try {
            MaterialResponse resp = materialManagementUseCase.updateMaterial(id, materialRequest);
            return new ResponseEntity<>(resp, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error("Invalid material data: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            log.error("Error updating material with id {}: {}", id, e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<MaterialResponse>> getAllMaterials() {
        try {
            List<MaterialResponse> materials = materialManagementUseCase.getAllMaterials();
            return new ResponseEntity<>(materials, HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Error retrieving materials: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMaterial(@PathVariable("id") Long id) {
        try {
            materialManagementUseCase.deleteMaterial(id);
            return new ResponseEntity<>("Material deleted successfully", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error("Material not found: {}", e.getMessage());
            return new ResponseEntity<>("Material not found", HttpStatus.NOT_FOUND);
        } catch (RuntimeException e) {
            log.error("Error deleting material: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/test-email")
    public ResponseEntity<String> testEmail(@RequestParam(value = "email", required = false) String email) {
        try {
            String targetEmail = email != null ? email : "test@example.com";
            emailService.sendEmail(targetEmail, "Test", "This is a test message.");
            return new ResponseEntity<>("Email sent to: " + targetEmail, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error("Invalid email parameter: {}", e.getMessage());
            return new ResponseEntity<>("Error sending email: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            log.error("Error sending email: {}", e.getMessage(), e);
            return new ResponseEntity<>("Error sending email: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}







