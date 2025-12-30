package com.techmate.techmate.hexagonal.infrastructure.imageStorage.Impl;

import com.techmate.techmate.hexagonal.infrastructure.exception.ValidationException;
import com.techmate.techmate.hexagonal.infrastructure.imageStorage.ImageStorageStrategy;
import com.techmate.techmate.hexagonal.infrastructure.validation.ImageValidationStrategy;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = "app.storage.type", havingValue = "minio")
public class MinioImageStorage implements ImageStorageStrategy {

    private final MinioClient minioClient;
    private final ImageValidationStrategy imageValidationStrategy;

    @Value("${minio.bucket:techshare-images}")
    private String bucket;

    public MinioImageStorage(ImageValidationStrategy imageValidationStrategy,
            @Value("${minio.endpoint:http://localhost:9000}") String endpoint,
            @Value("${minio.access-key:minioadmin}") String accessKey,
            @Value("${minio.secret-key:minioadmin}") String secretKey) {
        this.imageValidationStrategy = imageValidationStrategy;
        this.minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    @Override
    public String saveImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            return null;
        }

        imageValidationStrategy.validate(image);

        String originalFilename = image.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
        }

        String key = UUID.randomUUID().toString() + extension;

        try (InputStream in = image.getInputStream()) {
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(key)
                    .stream(in, image.getSize(), -1)
                    .contentType(image.getContentType())
                    .build();

            minioClient.putObject(putObjectArgs);

            return key; // Devolvemos la key; la URL puede construirse en capas superiores
        } catch (Exception e) {
            throw new RuntimeException("Error subiendo imagen a MinIO/S3", e);
        }
    }

    @Override
    public byte[] getImage(String filename) {
        try (InputStream is = minioClient.getObject(GetObjectArgs.builder().bucket(bucket).object(filename).build());
                ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            if (is == null) {
                throw new ValidationException("Imagen no encontrada: " + filename);
            }

            byte[] buffer = new byte[8192];
            int read;
            while ((read = is.read(buffer)) != -1) {
                baos.write(buffer, 0, read);
            }

            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error leyendo imagen desde MinIO/S3", e);
        }
    }

    @Override
    public void deleteImage(String imagePath) {
        if (imagePath == null)
            return;
        try {
            RemoveObjectArgs r = RemoveObjectArgs.builder().bucket(bucket).object(imagePath).build();
            minioClient.removeObject(r);
        } catch (Exception e) {
            // Log y continuar
            System.err.println("No se pudo eliminar objeto en MinIO: " + e.getMessage());
        }
    }
}

