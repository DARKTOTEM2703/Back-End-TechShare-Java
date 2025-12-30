package com.techmate.techmate.hexagonal.infrastructure.imageStorage.Impl;

import com.techmate.techmate.hexagonal.infrastructure.imageStorage.ImageStorageStrategy;
import com.techmate.techmate.hexagonal.infrastructure.validation.ImageValidationStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.UUID;

/**
 * Clase Maestra S3ImageStorage - Compatible con MinIO local Y AWS S3 real.
 * Usa software.amazon.awssdk que funciona con ambos.
 * NO dependencia de io.minio - S3 SDK maneja MinIO perfectamente.
 */
@Service
@Slf4j
@ConditionalOnProperty(name = "app.storage.type", havingValue = "minio", matchIfMissing = false)
public class S3ImageStorage implements ImageStorageStrategy {

    private final S3Client s3Client;
    private final ImageValidationStrategy imageValidationStrategy;
    private final String bucketName;
    private final String endpoint;

    public S3ImageStorage(ImageValidationStrategy imageValidationStrategy,
            @Value("${minio.endpoint:}") String endpoint,
            @Value("${minio.region:us-east-1}") String region,
            @Value("${minio.access-key:minioadmin}") String accessKey,
            @Value("${minio.secret-key:minioadmin}") String secretKey,
            @Value("${minio.bucket:techshare}") String bucketName) {

        this.imageValidationStrategy = imageValidationStrategy;
        this.bucketName = bucketName;
        this.endpoint = endpoint;

        S3ClientBuilder builder = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)));

        // 🔑 LA CLAVE: Si hay endpoint local (MinIO), forzamos path-style access
        if (endpoint != null && !endpoint.isBlank()) {
            builder.endpointOverride(URI.create(endpoint))
                    .forcePathStyle(true); // Vital para MinIO
            log.info("🚀 S3ImageStorage configurado para MinIO en: {}", endpoint);
        } else {
            log.info("☁️ S3ImageStorage configurado para AWS S3 Real");
        }

        this.s3Client = builder.build();
    }

    @Override
    public String saveImage(MultipartFile image) {
        return saveImage(image, "general");
    }

    public String saveImage(MultipartFile image, String directory) {
        if (image == null || image.isEmpty()) {
            return null;
        }

        imageValidationStrategy.validate(image);

        String extension = getExtension(image.getOriginalFilename());
        String key = String.format("%s/%s%s", directory, UUID.randomUUID(), extension);

        try (InputStream is = image.getInputStream()) {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(image.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(is, image.getSize()));

            log.info("✅ Imagen subida: {}", key);
            return buildUrl(key);

        } catch (IOException | S3Exception e) {
            log.error("❌ Error subiendo archivo a S3/MinIO: {}", e.getMessage());
            throw new RuntimeException("Error almacenando imagen: " + e.getMessage(), e);
        }
    }

    @Override
    public byte[] getImage(String filename) {
        String key = extractKeyFromUrl(filename);

        try {
            return s3Client.getObject(GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build()).readAllBytes();
        } catch (IOException | S3Exception e) {
            throw new RuntimeException("No se pudo recuperar la imagen: " + filename, e);
        }
    }

    @Override
    public void deleteImage(String imageUrl) {
        String key = extractKeyFromUrl(imageUrl);
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build());
            log.info("✅ Imagen eliminada: {}", key);
        } catch (S3Exception e) {
            log.warn("⚠️ No se pudo borrar imagen: {}", key);
        }
    }

    // --- Métodos Auxiliares ---

    private String getExtension(String filename) {
        return (filename != null && filename.contains("."))
                ? filename.substring(filename.lastIndexOf('.'))
                : "";
    }

    private String buildUrl(String key) {
        // Si es MinIO local, construimos URL accesible
        if (endpoint != null && !endpoint.isBlank()) {
            return String.format("%s/%s/%s", endpoint, bucketName, key);
        }
        // Si es AWS real, devolvemos la key (o URL de CloudFront si tuvieras)
        return key;
    }

    private String extractKeyFromUrl(String url) {
        // Si la URL contiene el endpoint, extraemos solo la key
        if (url != null && url.startsWith("http")) {
            int idx = url.lastIndexOf(bucketName);
            if (idx >= 0) {
                return url.substring(idx + bucketName.length() + 1);
            }
        }
        return url;
    }
}
