package com.techmate.techmate.imageStorage.Impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.techmate.techmate.imageStorage.ImageStorageStrategy;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.UUID;

/**
 * Implementación OPTIMIZADA de almacenamiento de imágenes en el sistema de archivos.
 * 
 * Características principales:
 * 1. COMPRESIÓN AUTOMÁTICA: Reduce el tamaño de las imágenes manteniendo calidad aceptable
 * 2. REDIMENSIONAMIENTO: Limita el tamaño máximo para ahorrar espacio y mejorar rendimiento
 * 3. CONVERSIÓN A JPEG: Estandariza el formato para optimizar compresión
 * 4. NOMBRES ÚNICOS: Usa UUID para evitar colisiones de nombres
 * 5. VALIDACIONES: Verifica tipos de archivo y tamaños
 * 
 * Ventajas vs FileSystemImageStorage básico:
 * - Menor uso de disco (imágenes más pequeñas)
 * - Carga más rápida en frontend (menos bytes a transferir)
 * - Mejor experiencia de usuario (páginas más rápidas)
 * - Prevención de ataques por archivos grandes
 * 
 * @author TechMate Team
 */
@Component("optimizedImageStorage") // Nombre específico para inyección selectiva
@Primary // Esta será la implementación por defecto cuando no se especifique @Qualifier
public class OptimizedImageStorage implements ImageStorageStrategy {

    // Ruta base donde se guardarán las imágenes (inyectada desde application.properties)
    @Value("${storage.location}")
    private String storageLocation;

    // Configuración de optimización
    private static final int MAX_WIDTH = 1920;  // Ancho máximo en píxeles
    private static final int MAX_HEIGHT = 1080; // Alto máximo en píxeles
    private static final float JPEG_QUALITY = 0.85f; // Calidad JPEG (0.0 a 1.0, 0.85 = 85%)
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB máximo

    // Extensiones permitidas (seguridad)
    private static final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif", ".webp"};

    /**
     * Guarda una imagen optimizándola automáticamente.
     * 
     * Proceso:
     * 1. Valida el archivo (extensión, tamaño, tipo MIME)
     * 2. Lee la imagen original
     * 3. Redimensiona si excede dimensiones máximas
     * 4. Comprime a JPEG con calidad configurable
     * 5. Guarda con nombre único (UUID)
     * 
     * @param image MultipartFile con la imagen subida
     * @return String con la ruta relativa de la imagen guardada (ej: "abc123.jpg")
     * @throws RuntimeException si falla la validación o el procesamiento
     */
    @Override
    public String saveImage(MultipartFile image) {
        // 1. VALIDACIONES INICIALES
        validateImage(image);

        try {
            // 2. CREAR DIRECTORIO SI NO EXISTE
            File uploadDir = new File(storageLocation);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs(); // Crear directorio y subdirectorios necesarios
            }

            // 3. GENERAR NOMBRE ÚNICO (UUID + .jpg)
            String uniqueFileName = UUID.randomUUID().toString() + ".jpg";
            Path destinationPath = Paths.get(storageLocation, uniqueFileName);

            // 4. LEER IMAGEN ORIGINAL
            BufferedImage originalImage = ImageIO.read(image.getInputStream());
            
            if (originalImage == null) {
                throw new RuntimeException("No se pudo leer la imagen. Formato no válido.");
            }

            // 5. REDIMENSIONAR SI ES NECESARIO
            BufferedImage resizedImage = resizeImageIfNeeded(originalImage);

            // 6. COMPRIMIR Y GUARDAR COMO JPEG
            saveAsOptimizedJPEG(resizedImage, destinationPath.toFile());

            // 7. RETORNAR RUTA RELATIVA (solo el nombre del archivo)
            return uniqueFileName;

        } catch (IOException e) {
            throw new RuntimeException("Error al guardar la imagen optimizada: " + e.getMessage(), e);
        }
    }

    /**
     * Elimina una imagen del sistema de archivos.
     * 
     * @param imagePath Nombre del archivo a eliminar (ej: "abc123.jpg")
     * @throws RuntimeException si falla la eliminación
     */
    @Override
    public void deleteImage(String imagePath) {
        // Validar que no sea null o vacío
        if (imagePath == null || imagePath.trim().isEmpty()) {
            return; // No hacer nada si no hay imagen
        }

        try {
            // Construir ruta completa y eliminar
            Path filePath = Paths.get(storageLocation, imagePath);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Error al eliminar la imagen: " + e.getMessage(), e);
        }
    }

    /**
     * Lee una imagen del sistema de archivos y la retorna como array de bytes.
     * Útil para servir imágenes vía HTTP.
     * 
     * @param filename Nombre del archivo a leer
     * @return byte[] con el contenido de la imagen
     * @throws RuntimeException si el archivo no existe o no se puede leer
     */
    @Override
    public byte[] getImage(String filename) {
        try {
            Path imagePath = Paths.get(storageLocation, filename);
            
            if (!Files.exists(imagePath)) {
                throw new RuntimeException("Imagen no encontrada: " + filename);
            }
            
            return Files.readAllBytes(imagePath);
        } catch (IOException e) {
            throw new RuntimeException("Error al leer la imagen: " + e.getMessage(), e);
        }
    }

    // ==================== MÉTODOS AUXILIARES (PRIVADOS) ====================

    /**
     * Valida que la imagen cumpla con los requisitos de seguridad y formato.
     * 
     * Validaciones:
     * - No null y no vacía
     * - Extensión permitida
     * - Tamaño dentro del límite
     * - Content-Type válido
     * 
     * @throws RuntimeException si alguna validación falla
     */
    private void validateImage(MultipartFile image) {
        // 1. Verificar que no sea null o vacío
        if (image == null || image.isEmpty()) {
            throw new RuntimeException("El archivo de imagen está vacío o es nulo");
        }

        // 2. Verificar tamaño
        if (image.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException(
                String.format("La imagen es demasiado grande. Máximo: %d MB", MAX_FILE_SIZE / (1024 * 1024))
            );
        }

        // 3. Verificar extensión
        String originalFilename = image.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new RuntimeException("Nombre de archivo inválido");
        }

        boolean hasValidExtension = false;
        String lowerFilename = originalFilename.toLowerCase();
        
        for (String ext : ALLOWED_EXTENSIONS) {
            if (lowerFilename.endsWith(ext)) {
                hasValidExtension = true;
                break;
            }
        }

        if (!hasValidExtension) {
            throw new RuntimeException(
                "Extensión de archivo no permitida. Permitidas: jpg, jpeg, png, gif, webp"
            );
        }

        // 4. Verificar content-type
        String contentType = image.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("El archivo no es una imagen válida");
        }
    }

    /**
     * Redimensiona la imagen si excede las dimensiones máximas permitidas.
     * Mantiene el aspect ratio (proporción) original.
     * 
     * Algoritmo:
     * - Calcula el factor de escala necesario (width y height)
     * - Usa el menor factor para mantener proporción
     * - Aplica redimensionamiento con interpolación de alta calidad
     * 
     * @param original BufferedImage original
     * @return BufferedImage redimensionada (o la original si ya es pequeña)
     */
    private BufferedImage resizeImageIfNeeded(BufferedImage original) {
        int originalWidth = original.getWidth();
        int originalHeight = original.getHeight();

        // Si ya es suficientemente pequeña, retornar sin modificar
        if (originalWidth <= MAX_WIDTH && originalHeight <= MAX_HEIGHT) {
            return original;
        }

        // Calcular nuevas dimensiones manteniendo aspect ratio
        double widthScale = (double) MAX_WIDTH / originalWidth;
        double heightScale = (double) MAX_HEIGHT / originalHeight;
        double scale = Math.min(widthScale, heightScale); // Usar el menor para que quepa

        int newWidth = (int) (originalWidth * scale);
        int newHeight = (int) (originalHeight * scale);

        // Crear nueva imagen redimensionada con calidad alta
        BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = resized.createGraphics();
        
        // Configurar renderizado de alta calidad
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Dibujar imagen escalada
        graphics.drawImage(original, 0, 0, newWidth, newHeight, null);
        graphics.dispose();

        return resized;
    }

    /**
     * Guarda la imagen como JPEG con compresión optimizada.
     * 
     * Ventajas del JPEG:
     * - Excelente compresión para fotografías
     * - Soporte universal en navegadores
     * - Tamaño de archivo reducido
     * 
     * @param image BufferedImage a guardar
     * @param outputFile File de destino
     * @throws IOException si falla la escritura
     */
    private void saveAsOptimizedJPEG(BufferedImage image, File outputFile) throws IOException {
        // Obtener el writer para JPEG
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        
        if (!writers.hasNext()) {
            throw new RuntimeException("No se encontró un encoder JPEG");
        }

        ImageWriter writer = writers.next();
        
        // Configurar parámetros de compresión
        ImageWriteParam writeParam = writer.getDefaultWriteParam();
        writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        writeParam.setCompressionQuality(JPEG_QUALITY); // 0.85 = 85% calidad

        // Escribir archivo
        try (FileOutputStream fos = new FileOutputStream(outputFile);
             ImageOutputStream ios = ImageIO.createImageOutputStream(fos)) {
            
            writer.setOutput(ios);
            writer.write(null, new IIOImage(image, null, null), writeParam);
        } finally {
            writer.dispose();
        }
    }
}


