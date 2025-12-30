package com.techmate.techmate.hexagonal.infrastructure.adapter.storage;

import com.techmate.techmate.hexagonal.application.port.output.ImageStoragePort;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.http.HttpUtils;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.RemoveObjectsArgs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class MinioImageStorageAdapter implements ImageStoragePort {
    private final MinioClient minioClient;
    private final String bucketName;

    public MinioImageStorageAdapter(MinioClient minioClient, @Value("${minio.bucket}") String bucketName) {
        this.minioClient = minioClient;
        this.bucketName = bucketName;
    }

    @Override
    public String upload(String fileName, InputStream fileStream, String contentType) {
        try {
            long size = fileStream.available();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(fileStream, size, -1)
                            .contentType(contentType)
                            .build());
            return fileName;
        } catch (Exception e) {
            throw new RuntimeException("Error uploading file to MinIO: " + e.getMessage(), e);
        }
    }

    @Override
    public String getPresignedUrl(String fileName) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(io.minio.http.Method.GET)
                            .bucket(bucketName)
                            .object(fileName)
                            .build());
        } catch (Exception e) {
            throw new RuntimeException("Error generating presigned URL: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String fileName) {
        try {
            List<DeleteObject> deleteObjects = new LinkedList<>();
            deleteObjects.add(new DeleteObject(fileName));
            minioClient.removeObjects(
                    RemoveObjectsArgs.builder()
                            .bucket(bucketName)
                            .objects(deleteObjects)
                            .build());
        } catch (Exception e) {
            throw new RuntimeException("Error deleting file from MinIO: " + e.getMessage(), e);
        }
    }
}
