package com.techmate.techmate.hexagonal.infrastructure.security;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.security.SecureRandom;
import java.util.Base64;

@Configuration
public class JwtConfig {
    private static final Logger log = LoggerFactory.getLogger(JwtConfig.class);

    @Value("${JWT_SECRET:}")
    private String jwtSecret;

    private final Environment env;

    public JwtConfig(Environment env) {
        this.env = env;
    }

    @PostConstruct
    public void init() {
        String[] activeProfiles = env.getActiveProfiles();
        String profile = (activeProfiles != null && activeProfiles.length > 0) ? activeProfiles[0]
                : env.getProperty("spring.profiles.active");

        boolean isTestOrDev = profile != null && (profile.equalsIgnoreCase("test") || profile.equalsIgnoreCase("dev"));

        if (jwtSecret == null || jwtSecret.isBlank()) {
            if (isTestOrDev) {
                // Generate a secure random 256-bit secret and base64-encode it. This avoids storing test secrets in repo.
                byte[] key = new byte[32]; // 256 bits
                new SecureRandom().nextBytes(key);
                String generated = Base64.getEncoder().encodeToString(key);
                jwtSecret = generated;
                log.info("No JWT_SECRET provided for profile '{}'. Generated an ephemeral test secret (not persisted).", profile);
            } else {
                log.warn("JWT_SECRET is not set via properties; application may fail in non-dev profiles.");
            }
        }

        TokenUtils.init(jwtSecret);
    }
}








