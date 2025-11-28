package com.techmate.techmate.config;

import com.techmate.techmate.security.JWTAuthenticationFilter;
import com.techmate.techmate.security.JwtRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Configuración de seguridad con JWT - CSRF deshabilitado
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);
    
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(UserDetailsService userDetailsService, 
                         PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        log.info("✅ SecurityConfig initialized");
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Permitir orígenes específicos y patterns
        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:*", "https://localhost:*", "http://127.0.0.1:*"));
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:3001", "http://127.0.0.1:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type", "Access-Control-Allow-Origin"));
        configuration.setMaxAge(3600L);
        log.info("🌐 CORS configurado para: localhost:3000, localhost:3001, patrones localhost:*");
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        log.info("✅ DaoAuthenticationProvider configured with UserDetailsService and PasswordEncoder");
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        // Forzar uso de nuestro DaoAuthenticationProvider personalizado
        ProviderManager providerManager = new ProviderManager(authenticationProvider());
        log.info("✅ AuthenticationManager configured with custom DaoAuthenticationProvider");
        return providerManager;
    }

    @Bean
    public JwtRequestFilter jwtRequestFilter() {
        return new JwtRequestFilter(userDetailsService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authManager, JwtRequestFilter jwtRequestFilter) throws Exception {
        log.info("🔒 Configuring SecurityFilterChain with JWT and CORS");
        
        // Crear filtro JWT para /login (autenticación)
        JWTAuthenticationFilter jwtAuthFilter = new JWTAuthenticationFilter();
        jwtAuthFilter.setAuthenticationManager(authManager);
        jwtAuthFilter.setFilterProcessesUrl("/login");
        
        // El filtro JWT se inyecta automáticamente como Bean
        // NO necesitamos crearlo manualmente aquí
        
        log.info("🔧 Adding JWT Request Filter to the security chain");
        
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Rutas públicas (login, register, verify, auth)
                .requestMatchers("/login", "/verify", "/api/auth/**").permitAll()
                .requestMatchers("/auth/**").permitAll()
                // Endpoint temporal para generar hash
                .requestMatchers("/temp/**").permitAll()
                // Health checks para monitoring
                .requestMatchers("/actuator/health", "/actuator/info", "/actuator/prometheus").permitAll()
                // Recursos estáticos e imágenes
                .requestMatchers("/admin/categories/images/**", "/admin/materials/images/**", 
                               "/admin/subcategories/images/**", "/uploaded-images/**").permitAll()
                // Rutas de administración - REQUIEREN ROL ADMIN
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // Endpoint de usuario autenticado
                .requestMatchers("/user/me").authenticated()
                // API general requiere autenticación
                .requestMatchers("/api/**").authenticated()
                // Cualquier otra petición requiere autenticación
                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider())
            .addFilter(jwtAuthFilter)
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        log.info("✅ SecurityFilterChain configured: CSRF disabled, CORS enabled, JWT active, ROLES enforced");
        return http.build();
    }
}


