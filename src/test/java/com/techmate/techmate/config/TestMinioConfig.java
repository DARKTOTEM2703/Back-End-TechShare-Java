package com.techmate.techmate.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import io.minio.MinioClient;
import org.mockito.Mockito;

@TestConfiguration
public class TestMinioConfig {

    @Bean
    public MinioClient minioClient() {
        return Mockito.mock(MinioClient.class);
    }
}
