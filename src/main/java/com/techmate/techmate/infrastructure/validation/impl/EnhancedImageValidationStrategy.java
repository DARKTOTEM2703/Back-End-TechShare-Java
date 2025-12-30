package com.techmate.techmate.infrastructure.validation.impl;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

/**
 * Estrategia de validación mejorada para imágenes.
 * 
 * <p>Valida múltiples aspectos de las imágenes subidas:</p>
 * <ul>
 *   <li>Tamaño máximo del archivo (10 MB por defecto)</li>
 *   <li>Tipo MIME permitido (JPEG, PNG, GIF, WebP)</li>
 *   <li>Extensión del archivo</li>
 *   <li>Que el archivo no esté vacío</li>
 * </ul>
 * 
 * <p><b>Uso:</b></p>
 * <pre>
 * {@code
 * @Autowired
 * private EnhancedImageValidationStrategy validator;
 * 
 * public void uploadImage(MultipartFile file) {
 *     validator.validate(file);  // Lanza excepción si no es válido
 *     // ... continuar con guardado
 * }
 * }
 * </pre>
 * 
 * @author TechShare Team
 * @version 2.0
 * @since 2025-10-02
 */
@Component
@Primary
public class EnhancedImageValidationStrategy implements com.techmate.techmate.infrastructure.validation.ImageValidationStrategy {

    // Tamaño máximo de archivo: 10 MB
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB en bytes
    
    // Tipos MIME permitidos
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
        "image/jpeg",
        "image/jpg",
        "image/png",
        "image/gif",
        "image/webp"
    );
    
    // Extensiones permitidas
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
        ".jpg", ".jpeg", ".png", ".gif", ".webp"
    );

    /**
     * Valida un archivo de imagen según múltiples criterios.
     * 
     * @param image El archivo MultipartFile a validar
     * @throws IllegalArgumentException Si la imagen no cumple con los requisitos
     */
    @Override
    public void validate(MultipartFile image) {
        // 1. Verificar que el archivo no sea nulo
        if (image == null) {
            throw new IllegalArgumentException("El archivo de imagen es requerido");
        }

        // 2. Verificar que el archivo no esté vacío
        if (image.isEmpty()) {
            throw new IllegalArgumentException("El archivo de imagen está vacío");
        }

        // 3. Validar tamaño máximo (10 MB)
        if (image.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                String.format("El archivo excede el tamaño máximo permitido de %.1f MB. Tamaño actual: %.2f MB",
                    MAX_FILE_SIZE / (1024.0 * 1024.0),
                    image.getSize() / (1024.0 * 1024.0))
            );
        }

        // 4. Validar tipo MIME
        String contentType = image.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException(
                "Tipo de archivo no permitido. Solo se aceptan: JPEG, PNG, GIF, WebP. " +
                "Tipo recibido: " + (contentType != null ? contentType : "desconocido")
            );
        }

        // 5. Validar extensión del archivo
        String filename = image.getOriginalFilename();
        if (filename == null || filename.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del archivo es inválido");
        }

        String extension = getFileExtension(filename).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException(
                "Extensión de archivo no permitida. Solo se aceptan: .jpg, .jpeg, .png, .gif, .webp. " +
                "Extensión recibida: " + extension
            );
        }

        // 6. Validar que el nombre no contenga caracteres peligrosos (prevenir path traversal)
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            throw new IllegalArgumentException(
                "El nombre del archivo contiene caracteres no permitidos"
            );
        }
    }

    /**
     * Extrae la extensión de un nombre de archivo.
     * 
     * @param filename El nombre del archivo
     * @return La extensión (incluyendo el punto), o cadena vacía si no hay extensión
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(lastDotIndex);
    }

    /**
     * Valida y retorna información del archivo en un formato legible.
     * 
     * @param image El archivo a validar
     * @return String con información del archivo validado
     */
    public String validateAndGetInfo(MultipartFile image) {
        validate(image);
        
        return String.format(
            "Archivo válido: %s | Tamaño: %.2f MB | Tipo: %s",
            image.getOriginalFilename(),
            image.getSize() / (1024.0 * 1024.0),
            image.getContentType()
        );
    }
}







