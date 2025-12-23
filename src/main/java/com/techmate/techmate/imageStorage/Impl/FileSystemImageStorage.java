package com.techmate.techmate.imageStorage.Impl;

import com.techmate.techmate.config.AppProperties;
import com.techmate.techmate.exception.ValidationException;
import com.techmate.techmate.imageStorage.ImageStorageStrategy;
import com.techmate.techmate.validation.ImageValidationStrategy;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Component
public class FileSystemImageStorage implements ImageStorageStrategy {

    private final ImageValidationStrategy imageValidationStrategy;
    private final AppProperties appProperties;

    // Inyección por constructor (Buenas prácticas)
    public FileSystemImageStorage(ImageValidationStrategy imageValidationStrategy, AppProperties appProperties) {
        this.imageValidationStrategy = imageValidationStrategy;
        this.appProperties = appProperties;
    }

    @Override
    public String saveImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            return null; // O lanzar excepción según tu lógica de negocio
        }

        // 1. Validar la imagen
        imageValidationStrategy.validate(image);

        try {
            // 2. Generar nombre único
            String originalFilename = image.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFilename = UUID.randomUUID().toString() + extension;

            // 3. Obtener ruta desde AppProperties
            String storageLocation = appProperties.getStorage().getLocation();
            Path uploadPath = Paths.get(storageLocation);

            // 4. Crear directorio si no existe
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 5. Guardar el archivo
            try (InputStream inputStream = image.getInputStream()) {
                Path filePath = uploadPath.resolve(newFilename);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }

            return newFilename;

        } catch (IOException e) {
            throw new RuntimeException("Error al guardar la imagen en el sistema de archivos", e);
        }
    }

    @Override
    public byte[] loadImage(String filename) {
        try {
            String storageLocation = appProperties.getStorage().getLocation();
            Path path = Paths.get(storageLocation).resolve(filename);

            if (!Files.exists(path)) {
                throw new ValidationException("La imagen no existe: " + filename);
            }

            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException("Error al leer la imagen", e);
        }
    }

    @Override
    public void deleteImage(String filename) {
        try {
            if (filename != null) {
                String storageLocation = appProperties.getStorage().getLocation();
                Path path = Paths.get(storageLocation).resolve(filename);
                Files.deleteIfExists(path);
            }
        } catch (IOException e) {
            // Loggear error pero no romper el flujo principal si el borrado falla
            System.err.println("No se pudo borrar la imagen: " + e.getMessage());
        }
    }
}
