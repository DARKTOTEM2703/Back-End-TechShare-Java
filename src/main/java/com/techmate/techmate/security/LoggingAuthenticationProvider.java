package com.techmate.techmate.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * AuthenticationProvider que añade trazas para depurar fallos de autenticación
 * sin exponer la contraseña en logs.
 */
public class LoggingAuthenticationProvider extends DaoAuthenticationProvider {

    private static final Logger log = LoggerFactory.getLogger(LoggingAuthenticationProvider.class);

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, org.springframework.security.authentication.UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        try {
            log.debug("additionalAuthenticationChecks: user='{}' enabled={} credentialsPresent={}", userDetails.getUsername(), userDetails.isEnabled(), authentication.getCredentials() != null);
            if (authentication.getCredentials() == null) {
                log.warn("Authentication attempt with null credentials for user {}", userDetails.getUsername());
            }
            super.additionalAuthenticationChecks(userDetails, authentication);
        } catch (BadCredentialsException e) {
            log.warn("Bad credentials for user {}: {}", userDetails.getUsername(), e.getMessage());
            throw e;
        } catch (AuthenticationException e) {
            log.warn("Authentication exception for user {}: {}", userDetails.getUsername(), e.getMessage());
            throw e;
        }
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.debug("authenticate called for principal={}", authentication.getPrincipal());
        return super.authenticate(authentication);
    }
}

