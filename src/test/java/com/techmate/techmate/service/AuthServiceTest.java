package com.techmate.techmate.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Optional;

import com.techmate.techmate.infrastructure.config.AppProperties;
import com.techmate.techmate.infrastructure.dto.RegisterRequest;
import com.techmate.techmate.core.domain.entity.Usuario;
import com.techmate.techmate.core.domain.entity.VerificationToken;
import com.techmate.techmate.infrastructure.service.mapper.AuthMapper;
import com.techmate.techmate.infrastructure.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.techmate.techmate.infrastructure.persistence.repository.UsuarioRepository;
import com.techmate.techmate.infrastructure.persistence.repository.VerificationTokenRepository;
import com.techmate.techmate.infrastructure.persistence.repository.RoleRepository;
import com.techmate.techmate.infrastructure.persistence.repository.UsuarioRoleRepository;
import com.techmate.techmate.infrastructure.service.EmailService;
import com.techmate.techmate.infrastructure.service.EmailTemplateService;

class AuthServiceTest {

    @Mock
    UsuarioRepository usuarioRepository;

    @Mock
    VerificationTokenRepository verificationTokenRepository;

    @Mock
    RoleRepository roleRepository;

    @Mock
    UsuarioRoleRepository usuarioRoleRepository;

    @Mock
    EmailService emailService;

    @Mock
    EmailTemplateService emailTemplateService;

    @Mock
    AppProperties appProperties;

    AuthMapper authMapper = new AuthMapper();

    AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock AppProperties and its nested configuration
        AppProperties.Verification verification = new AppProperties.Verification();
        verification.setUrl("http://localhost:8080/verify?token=");
        when(appProperties.getVerification()).thenReturn(verification);

        // Mock EmailTemplateService
        when(emailTemplateService.generateVerificationEmail(anyString(), anyString()))
                .thenReturn("<html>Test Email</html>");

        // Mock roleRepository para retornar rol 'USER'
        com.techmate.techmate.infrastructure.persistence.entity.Role userRole = new com.techmate.techmate.infrastructure.persistence.entity.Role();
        userRole.setId(2);
        userRole.setName("USER");
        when(roleRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.of(userRole));

        authService = new AuthService(usuarioRepository, verificationTokenRepository, roleRepository,
                usuarioRoleRepository, emailService, emailTemplateService, authMapper, appProperties);
    }

    @Test
    void registerUser_success() {
        RegisterRequest req = new RegisterRequest();
        req.setUser_name("jdoe");
        req.setFirst_name("John");
        req.setLast_name("Doe");
        req.setEmail("jdoe@example.com");
        req.setPassword("encoded");

        when(usuarioRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        String res = authService.registerUser(req);

        assertTrue(res.contains("Usuario registrado"));
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
        verify(verificationTokenRepository, times(1)).save(any(VerificationToken.class));
        verify(emailService, times(1)).sendHtmlEmail(anyString(), anyString(), anyString());
    }

    @Test
    void registerUser_duplicate_throws() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("exists@example.com");

        when(usuarioRepository.findByEmail(any())).thenReturn(Optional.of(new Usuario()));

        assertThrows(IllegalArgumentException.class, () -> authService.registerUser(req));
    }
}
