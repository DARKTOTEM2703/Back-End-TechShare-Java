package com.techmate.techmate.infrastructure.health;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListBucketsRequest;

/**
 * Custom Health Indicator para MinIO/S3 Storage.
 * 
 * Expone estado del servicio de almacenamiento en:
 * GET /actuator/health
 * 
 * Ejemplo de respuesta:
 * {
 * "status": "UP",
 * "components": {
 * "storage": {
 * "status": "UP",
 * "details": {
 * "service": "minio",
 * "buckets": 3,
 * "responseTime": "45ms"
 * }
 * }
 * }
 * }
 */
@Component("storageHealth")
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.storage.type", havingValue = "minio")
public class StorageHealthIndicator implements HealthIndicator {

    private final S3Client s3Client;

    @Override
    public Health health() {
        try {
            long startTime = System.currentTimeMillis();

            // Intentar listar buckets (operación ligera)
            var bucketsResponse = s3Client.listBuckets(ListBucketsRequest.builder().build());
            int bucketCount = bucketsResponse.buckets().size();

            long responseTime = System.currentTimeMillis() - startTime;

            return Health.up()
                    .withDetail("service", "minio/s3")
                    .withDetail("status", "available")
                    .withDetail("buckets", bucketCount)
                    .withDetail("responseTime", responseTime + "ms")
                    .build();

        } catch (Exception ex) {
            log.error("Storage health check failed", ex);

            return Health.down()
                    .withDetail("service", "minio/s3")
                    .withDetail("status", "unavailable")
                    .withDetail("error", ex.getMessage())
                    .withException(ex)
                    .build();
        }
    }
}
