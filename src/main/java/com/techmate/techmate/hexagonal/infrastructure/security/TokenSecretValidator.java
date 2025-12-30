package com.techmate.techmate.hexagonal.infrastructure.security;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class TokenSecretValidator {
    private static final Logger log = LoggerFactory.getLogger(TokenSecretValidator.class);

    private final Environment env;

    public TokenSecretValidator(Environment env) {
        this.env = env;
    }

    @PostConstruct
    public void validate() {
        String secret = env.getProperty("JWT_SECRET");

        String[] activeProfiles = env.getActiveProfiles();
        String profile = (activeProfiles != null && activeProfiles.length > 0) ? activeProfiles[0]
                : env.getProperty("spring.profiles.active");

        boolean isTestOrDev = profile != null && (profile.equalsIgnoreCase("test") || profile.equalsIgnoreCase("dev"));

        if (secret == null || secret.isBlank()) {
            if (isTestOrDev) {
                log.warn("No JWT_SECRET provided for profile '{}'. JwtConfig may generate an ephemeral secret for tests/dev.", profile);
                return;
            }
            log.error("Missing JWT_SECRET property and active profile is '{}'. Aborting startup.", profile);
            throw new IllegalStateException("Missing JWT_SECRET property. Set JWT_SECRET in production.");
        }

        // Validate secret length: expect at least 32 bytes when used as raw key; if base64 encoded, length >= 43 chars
        boolean ok = false;
        try {
            byte[] decoded = java.util.Base64.getDecoder().decode(secret);
            if (decoded.length >= 32) ok = true;
        } catch (IllegalArgumentException ex) {
            // Not base64, check raw length
            if (secret.getBytes().length >= 32) ok = true;
        }

        if (!ok && !isTestOrDev) {
            log.error("JWT_SECRET does not meet minimum length/entropy requirements for profile '{}'. Aborting startup.", profile);
            throw new IllegalStateException("JWT_SECRET is too short or weak for production.");
        }

        log.debug("JWT secret found in environment/properties for profile '{}'. Length validated: {}", profile, ok);
    }
}








