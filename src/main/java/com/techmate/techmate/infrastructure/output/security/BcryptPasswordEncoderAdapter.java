package com.techmate.techmate.infrastructure.output.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techmate.techmate.core.application.port.output.PasswordEncoderPort;

/**
 * Password encoder adapter using Spring's BCryptPasswordEncoder.
 * 
 * Implements PasswordEncoderPort to provide password encoding/matching
 * functionality.
 * Uses Spring Security's BCryptPasswordEncoder for secure password handling.
 */
@Component
public class BcryptPasswordEncoderAdapter implements PasswordEncoderPort {

    private static final Logger logger = LoggerFactory.getLogger(BcryptPasswordEncoderAdapter.class);

    private final PasswordEncoder passwordEncoder;

    public BcryptPasswordEncoderAdapter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String encode(String plainPassword) {
        logger.debug("Encoding password");
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Plain password cannot be null or empty");
        }
        return passwordEncoder.encode(plainPassword);
    }

    @Override
    public boolean matches(String plainPassword, String encodedPassword) {
        logger.debug("Matching password with encoded value");
        if (plainPassword == null || plainPassword.isEmpty()) {
            logger.warn("Plain password is null or empty");
            return false;
        }
        if (encodedPassword == null || encodedPassword.isEmpty()) {
            logger.warn("Encoded password is null or empty");
            return false;
        }
        return passwordEncoder.matches(plainPassword, encodedPassword);
    }
}
