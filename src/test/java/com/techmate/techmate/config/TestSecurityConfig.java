package com.techmate.techmate.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.techmate.techmate.infrastructure.security.TokenUtils;

@TestConfiguration
public class TestSecurityConfig {

    @Bean
    public TokenUtils tokenUtils() {
        // Inicializar el secreto para TokenUtils y devolver una instancia para
        // inyección
        TokenUtils.init("test_secret_key_minimum_32_bytes_required_for_hs256");
        return new TokenUtils();
    }
}
