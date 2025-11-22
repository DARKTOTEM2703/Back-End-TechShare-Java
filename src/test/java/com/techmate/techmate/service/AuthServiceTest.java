package com.techmate.techmate.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import com.techmate.techmate.config.AppProperties;
import com.techmate.techmate.dto.RegisterRequest;
import com.techmate.techmate.entity.Usuario;
import com.techmate.techmate.entity.VerificationToken;
import com.techmate.techmate.repository.RoleRepository;
import com.techmate.techmate.repository.UsuarioRepository;
import com.techmate.techmate.repository.VerificationTokenRepository;
import com.techmate.techmate.service.mapper.AuthMapper;
import com.techmate.techmate.repository.UsuarioRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

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

        // Mock roleRepository para retornar rol 'user' con ID 2
        com.techmate.techmate.entity.Role userRole = new com.techmate.techmate.entity.Role();
        userRole.setId(2);
        userRole.setNombre("user");
        when(roleRepository.findById(2)).thenReturn(Optional.of(userRole));
        when(roleRepository.findByName("user")).thenReturn(Optional.of(userRole));

        authService = new AuthService(usuarioRepository, verificationTokenRepository, roleRepository,
                usuarioRoleRepository, emailService, emailTemplateService, authMapper, appProperties);
    }

    @Test
    void registerUser_success() throws jakarta.mail.MessagingException {
        RegisterRequest req = new RegisterRequest();
        req.setUser_name("jdoe");
        req.setFirst_name("John");
        req.setLast_name("Doe");
        req.setEmail("jdoe@example.com");
        req.setPassword("encoded");

        when(usuarioRepository.findOneByEmail(any())).thenReturn(Optional.empty());
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

        when(usuarioRepository.findOneByEmail(any())).thenReturn(Optional.of(new Usuario()));

        assertThrows(IllegalArgumentException.class, () -> authService.registerUser(req));
    }
}
