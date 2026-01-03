package com.techmate.techmate.infrastructure.adapter.storage;

import com.techmate.techmate.application.port.output.ImageStoragePort;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectsArgs;
import io.minio.messages.DeleteObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Primary;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Component
@Primary
@ConditionalOnProperty(name = "minio.enabled", havingValue = "true", matchIfMissing = true)
public class MinioImageStorageAdapter implements ImageStoragePort {
    private final MinioClient minioClient;
    private final String bucketName;

    public MinioImageStorageAdapter(@Value("${minio.endpoint}") String endpoint,
            @Value("${minio.access-key}") String accessKey,
            @Value("${minio.secret-key}") String secretKey,
            @Value("${minio.bucket}") String bucketName) {
        this.minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
        this.bucketName = bucketName;
    }

    @Override
    public String saveImage(MultipartFile file, String folderName) {
        try {
            String fileName = generateFileName(file.getOriginalFilename(), folderName);
            long size = file.getSize();

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(file.getInputStream(), size, -1)
                            .contentType(file.getContentType())
                            .build());
            return fileName;
        } catch (Exception e) {
            throw new RuntimeException("Error uploading file to MinIO: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteImage(String imageUrl) {
        try {
            List<DeleteObject> deleteObjects = new LinkedList<>();
            deleteObjects.add(new DeleteObject(imageUrl));
            minioClient.removeObjects(
                    RemoveObjectsArgs.builder()
                            .bucket(bucketName)
                            .objects(deleteObjects)
                            .build());
        } catch (Exception e) {
            throw new RuntimeException("Error deleting file from MinIO: " + e.getMessage(), e);
        }
    }

    private String generateFileName(String originalFileName, String folderName) {
        String extension = (originalFileName != null && originalFileName.contains("."))
                ? originalFileName.substring(originalFileName.lastIndexOf('.'))
                : "";
        return String.format("%s/%s%s", folderName, UUID.randomUUID(), extension);
    }
}
