package com.techmate.techmate.hexagonal.infrastructure.service;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.techmate.techmate.hexagonal.infrastructure.dto.VerificationResponse;
import com.techmate.techmate.hexagonal.domain.entity.Usuario;
import com.techmate.techmate.hexagonal.domain.entity.VerificationToken;
import com.techmate.techmate.hexagonal.domain.repository.UsuarioRepository;
import com.techmate.techmate.hexagonal.domain.repository.VerificationTokenRepository;

@Service
public class VerificationService {
    private final VerificationTokenRepository verificationTokenRepository;
    private final UsuarioRepository usuarioRepository;
    private static final Logger log = LoggerFactory.getLogger(VerificationService.class);

    public VerificationService(VerificationTokenRepository verificationTokenRepository, UsuarioRepository usuarioRepository) {
        this.verificationTokenRepository = verificationTokenRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public VerificationResponse verifyToken(String token) {
        try {
            VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
            if (verificationToken == null) {
                log.debug("verifyToken: token not found: {}", token);
                return new VerificationResponse(false, "Token inválido.");
            }
            log.debug("verifyToken: found token={}, expiry={} for user={}", verificationToken.getToken(), verificationToken.getExpiryDate(), verificationToken.getUsuario().getEmail());
            Usuario usuario = verificationToken.getUsuario();
            Calendar cal = Calendar.getInstance();
            long diff = verificationToken.getExpiryDate().getTime() - cal.getTime().getTime();
            if (diff <= 0) {
                log.debug("verifyToken: token expired (expiry={}, now={}, diff={})", verificationToken.getExpiryDate(), cal.getTime(), diff);
                return new VerificationResponse(false, "Token caducado.");
            }

            // Activar la cuenta usando una query nativa para evitar cascadas de relaciones
            usuarioRepository.enableUserById(usuario.getId());
            log.info("Usuario verificado y habilitado: {}", usuario.getEmail());

            return new VerificationResponse(true, "Cuenta verificada con éxito. Ahora puedes iniciar sesión.");
        } catch (Exception e) {
            log.error("Error verificando token: ", e);
            return new VerificationResponse(false, "Error al verificar el token. Intenta de nuevo o contacta soporte.");
        }
    }
}









