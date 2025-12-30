package com.techmate.techmate.hexagonal.infrastructure.imageStorage.Impl;

import com.techmate.techmate.hexagonal.infrastructure.exception.ValidationException;
import com.techmate.techmate.hexagonal.infrastructure.imageStorage.ImageStorageStrategy;
import com.techmate.techmate.hexagonal.infrastructure.validation.ImageValidationStrategy;
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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = "app.storage.type", havingValue = "s3")
public class AwsS3ImageStorage implements ImageStorageStrategy {

    private final S3Client s3;
    private final ImageValidationStrategy imageValidationStrategy;

    @Value("${aws.s3.bucket:techshare-images}")
    private String bucket;

    public AwsS3ImageStorage(ImageValidationStrategy imageValidationStrategy,
            @Value("${aws.s3.endpoint:}") String endpoint,
            @Value("${aws.s3.region:us-east-1}") String region,
            @Value("${aws.s3.access-key:}") String accessKey,
            @Value("${aws.s3.secret-key:}") String secretKey) {
        this.imageValidationStrategy = imageValidationStrategy;

        S3ClientBuilder builder = S3Client.builder();

        if (endpoint != null && !endpoint.isBlank()) {
            builder.endpointOverride(URI.create(endpoint));
        }

        if (region != null && !region.isBlank()) {
            builder.region(Region.of(region));
        }

        if (accessKey != null && !accessKey.isBlank() && secretKey != null && !secretKey.isBlank()) {
            builder.credentialsProvider(
                    StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)));
        }

        this.s3 = builder.build();
    }

    @Override
    public String saveImage(MultipartFile image) {
        if (image == null || image.isEmpty())
            return null;

        imageValidationStrategy.validate(image);

        String originalFilename = image.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
        }

        String key = UUID.randomUUID().toString() + extension;

        try (InputStream is = image.getInputStream()) {
            PutObjectRequest por = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(image.getContentType())
                    .build();

            s3.putObject(por, RequestBody.fromInputStream(is, image.getSize()));

            return key;
        } catch (Exception e) {
            throw new RuntimeException("Error subiendo imagen a S3", e);
        }
    }

    @Override
    public byte[] getImage(String filename) {
        try (InputStream is = s3.getObject(GetObjectRequest.builder().bucket(bucket).key(filename).build());
                ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            if (is == null)
                throw new ValidationException("Imagen no encontrada: " + filename);

            byte[] buffer = new byte[8192];
            int r;
            while ((r = is.read(buffer)) != -1)
                baos.write(buffer, 0, r);

            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error leyendo imagen desde S3", e);
        }
    }

    @Override
    public void deleteImage(String imagePath) {
        if (imagePath == null)
            return;
        try {
            DeleteObjectRequest dor = DeleteObjectRequest.builder().bucket(bucket).key(imagePath).build();
            s3.deleteObject(dor);
        } catch (Exception e) {
            System.err.println("No se pudo eliminar objeto en S3: " + e.getMessage());
        }
    }
}

