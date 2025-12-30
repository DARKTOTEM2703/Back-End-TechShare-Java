package com.techmate.techmate.hexagonal.infrastructure.imageStorage.Impl;

import com.techmate.techmate.config.AppProperties;
import com.techmate.techmate.hexagonal.infrastructure.exception.ValidationException;
import com.techmate.techmate.hexagonal.infrastructure.imageStorage.ImageStorageStrategy;
import com.techmate.techmate.hexagonal.infrastructure.validation.ImageValidationStrategy;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
@Primary // Esta es la estrategia por defecto
public class OptimizedImageStorage implements ImageStorageStrategy {

    private final ImageValidationStrategy imageValidationStrategy;
    private final AppProperties appProperties;

    // Inyección por constructor
    public OptimizedImageStorage(ImageValidationStrategy imageValidationStrategy, AppProperties appProperties) {
        this.imageValidationStrategy = imageValidationStrategy;
        this.appProperties = appProperties;
    }

    @Override
    public String saveImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            return null;
        }

        // 1. Validar
        imageValidationStrategy.validate(image);

        try {
            // 2. Obtener ruta de configuración
            String storageLocation = appProperties.getStorage().getLocation();
            Path rootLocation = Paths.get(storageLocation);

            if (!Files.exists(rootLocation)) {
                Files.createDirectories(rootLocation);
            }

            // 3. Generar nombre único
            String extension = getExtension(image.getOriginalFilename());
            String newFilename = UUID.randomUUID().toString() + extension;
            Path destinationFile = rootLocation.resolve(newFilename);

            // 4. Optimizar y Guardar (Usando Thumbnailator)
            // Comprime y redimensiona si es muy grande, manteniendo ratio
            Thumbnails.of(image.getInputStream())
                    .size(1024, 1024) // Max 1024px ancho o alto
                    .outputQuality(0.8) // Compresión JPEG/PNG al 80%
                    .toFile(destinationFile.toFile());

            return newFilename;

        } catch (IOException e) {
            throw new RuntimeException("Error al guardar y optimizar la imagen", e);
        }
    }

    @Override
    public byte[] getImage(String filename) {
        try {
            String storageLocation = appProperties.getStorage().getLocation();
            Path file = Paths.get(storageLocation).resolve(filename);

            if (Files.exists(file) && Files.isReadable(file)) {
                return Files.readAllBytes(file);
            } else {
                throw new ValidationException("No se pudo leer la imagen o no existe: " + filename);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al cargar la imagen", e);
        }
    }

    @Override
    public void deleteImage(String filename) {
        if (filename == null)
            return;
        try {
            String storageLocation = appProperties.getStorage().getLocation();
            Path file = Paths.get(storageLocation).resolve(filename);
            Files.deleteIfExists(file);
        } catch (IOException e) {
            // Log y continuar
            System.err.println("Advertencia: No se pudo eliminar la imagen " + filename);
        }
    }

    private String getExtension(String filename) {
        if (filename == null)
            return ".jpg"; // Default safe
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1) ? ".jpg" : filename.substring(dotIndex);
    }
}

