package com.techmate.techmate.hexagonal.infrastructure.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techmate.techmate.hexagonal.infrastructure.config.AppProperties;
import com.techmate.techmate.hexagonal.infrastructure.dto.RegisterRequest;
import com.techmate.techmate.hexagonal.domain.entity.Usuario;
import com.techmate.techmate.hexagonal.domain.entity.UsuarioRole;
import com.techmate.techmate.hexagonal.domain.entity.Role;
import com.techmate.techmate.hexagonal.domain.entity.VerificationToken;
import com.techmate.techmate.hexagonal.domain.repository.UsuarioRepository;
import com.techmate.techmate.hexagonal.domain.repository.VerificationTokenRepository;
import com.techmate.techmate.hexagonal.infrastructure.service.mapper.AuthMapper;
import com.techmate.techmate.hexagonal.domain.repository.RoleRepository;
import com.techmate.techmate.hexagonal.domain.repository.UsuarioRoleRepository;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UsuarioRepository usuarioRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final RoleRepository roleRepository;
    private final UsuarioRoleRepository usuarioRoleRepository;
    private final EmailService emailService;
    private final EmailTemplateService emailTemplateService;
    private final AuthMapper authMapper;
    private final AppProperties appProperties;

    public AuthService(UsuarioRepository usuarioRepository,
            VerificationTokenRepository verificationTokenRepository,
            RoleRepository roleRepository,
            UsuarioRoleRepository usuarioRoleRepository,
            EmailService emailService,
            EmailTemplateService emailTemplateService,
            AuthMapper authMapper,
            AppProperties appProperties) {
        this.usuarioRepository = usuarioRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.roleRepository = roleRepository;
        this.usuarioRoleRepository = usuarioRoleRepository;
        this.emailService = emailService;
        this.emailTemplateService = emailTemplateService;
        this.authMapper = authMapper;
        this.appProperties = appProperties;
    }

    @Transactional
    public String registerUser(RegisterRequest registerRequest) {
        if (usuarioRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new IllegalArgumentException("El usuario ya existe");
        }

        Usuario usuario = authMapper.toEntity(registerRequest);

        // Guardar el usuario primero para obtener su ID
        usuarioRepository.save(usuario);

        // Buscar rol por nombre (case-insensitive) y crear si no existe
        Role userRole = roleRepository.findByNameIgnoreCase("USER")
                .orElseGet(() -> {
                    Role r = new Role();
                    r.setName("USER");
                    return roleRepository.save(r);
                });

        UsuarioRole usuarioRole = new UsuarioRole();
        usuarioRole.setUsuario(usuario);
        usuarioRole.setRole(userRole);
        usuarioRoleRepository.save(usuarioRole);

        log.info("Rol 'USER' asignado automáticamente al usuario: {}", usuario.getEmail());

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, usuario);
        verificationTokenRepository.save(verificationToken);

        String verificationUrl = appProperties.getVerification().getUrl() + token;

        // Generar plantilla HTML profesional para el email
        String userName = usuario.getFirst_name() != null ? usuario.getFirst_name() : usuario.getUser_name();

        try {
            log.info("Encolando email de verificación (async) para usuario: {}", userName);
            String htmlContent = emailTemplateService.generateVerificationEmail(userName, verificationUrl);
            // send asynchronously; EmailService swallows/logs errors
            emailService.sendHtmlEmail(usuario.getEmail(), "Verificación de cuenta - TechShare", htmlContent);
        } catch (RuntimeException e) {
            // If template generation fails, try a simple text email asynchronously
            log.error("Fallo generando plantilla de email, encolando fallback de texto", e);
            try {
                emailService.sendEmail(usuario.getEmail(), "Verificación de cuenta",
                        "Por favor, verifica tu cuenta: " + verificationUrl);
            } catch (RuntimeException ex) {
                // EmailService is async and resilient; log and continue. Do NOT throw to avoid
                // 500 to client.
                log.error("No se pudo encolar email de verificación para {}", usuario.getEmail(), ex);
            }
        }

        return "Usuario registrado con éxito. Revisa tu correo para verificar tu cuenta.";
    }

    @Transactional
    public String resendVerification(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email inválido");
        }
        String normalized = email.toLowerCase().trim();
        Optional<Usuario> opt = usuarioRepository.findByEmail(normalized);
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        Usuario usuario = opt.get();
        if (usuario.isEnabled()) {
            return "La cuenta ya está verificada.";
        }

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, usuario);
        verificationTokenRepository.save(verificationToken);

        String verificationUrl = appProperties.getVerification().getUrl() + token;

        String userName = usuario.getFirst_name() != null ? usuario.getFirst_name() : usuario.getUser_name();
        try {
            log.info("Encolando email de verificación (resend) para usuario: {}", userName);
            String htmlContent = emailTemplateService.generateVerificationEmail(userName, verificationUrl);
            emailService.sendHtmlEmail(usuario.getEmail(), "Verificación de cuenta - TechShare", htmlContent);
        } catch (RuntimeException e) {
            log.error("Fallo generando plantilla de email en resend, encolando fallback de texto", e);
            try {
                emailService.sendEmail(usuario.getEmail(), "Verificación de cuenta",
                        "Por favor, verifica tu cuenta: " + verificationUrl);
            } catch (RuntimeException ex) {
                log.error("No se pudo encolar email de verificación (resend) para {}", usuario.getEmail(), ex);
            }
        }

        return "Correo de verificación reenviado. Revisa tu bandeja de entrada.";
    }
}







