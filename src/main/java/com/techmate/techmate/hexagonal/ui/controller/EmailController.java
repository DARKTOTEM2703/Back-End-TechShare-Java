package com.techmate.techmate.hexagonal.ui.controller;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.techmate.techmate.hexagonal.infrastructure.service.EmailService;
import com.techmate.techmate.hexagonal.infrastructure.validation.SafeString;

@RestController
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/sendEmail")
    public String sendEmail(
            @RequestParam @Email(message = "El campo 'to' debe ser un email válido") String to,

            @RequestParam @NotBlank(message = "El asunto no puede estar vacío") @Size(min = 3, max = 200, message = "El asunto debe tener entre 3 y 200 caracteres") @SafeString(allowSpecial = true) String subject,

            @RequestParam @NotBlank(message = "El cuerpo del mensaje no puede estar vacío") @Size(min = 5, max = 5000, message = "El mensaje debe tener entre 5 y 5000 caracteres") @SafeString(allowSpecial = true) String text) {
        emailService.sendEmail(to, subject, text);
        return "Email sent successfully!";
    }
}


