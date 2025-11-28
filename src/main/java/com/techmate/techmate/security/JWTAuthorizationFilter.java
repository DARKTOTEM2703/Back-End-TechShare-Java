package com.techmate.techmate.security;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.GenericFilterBean;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filtro de autorización JWT usando GenericFilterBean
 */
public class JWTAuthorizationFilter extends GenericFilterBean {

    private static final Logger log = LoggerFactory.getLogger(JWTAuthorizationFilter.class);
    
    private final UserDetailsService userDetailsService;
    
    public JWTAuthorizationFilter(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
        log.info("🚀 JWTAuthorizationFilter initialized as GenericFilterBean");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String header = httpRequest.getHeader("Authorization");
        String requestURI = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();
        
        log.info("🔍 JWT Filter Processing - {} {}", method, requestURI);
        
        if (header != null && header.startsWith("Bearer ")) {
            try {
                String token = header.replace("Bearer ", "");
                String email = TokenUtils.getUsernameFromToken(token);
                
                log.info("🔐 Token validation - extracted email: {}", email);
                
                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                    
                    if (TokenUtils.validateToken(token, userDetails)) {
                        log.info("✅ Token valid for user: {}", email);
                        
                        UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
                        
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        log.info("🔒 Authentication set in SecurityContext for user: {}", email);
                    } else {
                        log.warn("❌ Token validation failed for user: {}", email);
                    }
                }
            } catch (Exception e) {
                log.error("❌ Error processing JWT token: {}", e.getMessage(), e);
                SecurityContextHolder.clearContext();
            }
        } else {
            log.debug("No Bearer token found for {} {}", method, requestURI);
        }
        
        chain.doFilter(request, response);
    }
}