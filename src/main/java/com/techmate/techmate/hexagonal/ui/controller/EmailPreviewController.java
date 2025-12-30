package com.techmate.techmate.hexagonal.ui.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.techmate.techmate.hexagonal.infrastructure.service.EmailTemplateService;

/**
 * Controlador temporal para preview de emails
 * SOLO PARA DESARROLLO - Eliminar en producción
 * 
 * ⚠️ DESHABILITADO EN PRODUCCIÓN
 */
// @RestController
// @RequestMapping("/api/dev")
public class EmailPreviewController {

    private final EmailTemplateService emailTemplateService;

    public EmailPreviewController(EmailTemplateService emailTemplateService) {
        this.emailTemplateService = emailTemplateService;
    }

    /**
     * Preview del email de verificación
     * URL: http://localhost:8080/api/dev/preview/verification?userName=Juan&token=abc123
     */
    @GetMapping(value = "/preview/verification", produces = MediaType.TEXT_HTML_VALUE)
    public String previewVerificationEmail(
            @RequestParam(defaultValue = "Usuario Demo") String userName,
            @RequestParam(defaultValue = "ejemplo-token-uuid-12345") String token) {
        
        String verificationUrl = "http://localhost:3000/verify?token=" + token;
        return emailTemplateService.generateVerificationEmail(userName, verificationUrl);
    }

    /**
     * Preview del email de bienvenida
     * URL: http://localhost:8080/api/dev/preview/welcome?userName=Juan
     */
    @GetMapping(value = "/preview/welcome", produces = MediaType.TEXT_HTML_VALUE)
    public String previewWelcomeEmail(
            @RequestParam(defaultValue = "Usuario Demo") String userName) {
        
        return emailTemplateService.generateWelcomeEmail(userName);
    }
}




