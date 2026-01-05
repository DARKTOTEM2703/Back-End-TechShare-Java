package com.techmate.techmate.presentation.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.techmate.techmate.infrastructure.dto.VerificationResponse;
import com.techmate.techmate.infrastructure.service.VerificationService;

@RestController
@RequestMapping("/auth")  //Agregar prefijo /auth
public class TokenVeriController {

    private final VerificationService verificationService;

    // Origen del frontend para redirecciones cuando la petición viene de un navegador
    @Value("${FRONTEND_ORIGIN:http://localhost:3000}")
    private String frontendOrigin;

    public TokenVeriController(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    /**
     * Verifica el token de activación. Comporta dos modos:
     * - Si el cliente acepta JSON (API) devuelve un JSON/texto con el resultado.
     * - Si la petición proviene de un navegador (Accept contiene text/html) redirige al frontend
     *   con query params claros para que la SPA muestre la pantalla correspondiente.
     */
    @GetMapping("/verify")
    public ResponseEntity<String> verifyAccount(@RequestParam("token") String token,
                                                HttpServletRequest request,
                                                HttpServletResponse response) throws Exception {

        VerificationResponse result = verificationService.verifyToken(token);

        // Si el cliente espera HTML, hacemos redirección al frontend (p. ej. cuando se pulsa el link del email)
        String accept = request.getHeader("Accept");
        boolean wantsHtml = accept != null && accept.contains(MediaType.TEXT_HTML_VALUE);

        if (wantsHtml) {
            // Construir URL de redirect hacia el frontend. Incluimos status y mensaje (url-encoded)
            String target;
            if (result.isSuccess()) {
                target = String.format("%s/verify?success=true&message=%s", frontendOrigin, java.net.URLEncoder.encode(result.getMessage(), java.nio.charset.StandardCharsets.UTF_8));
            } else {
                target = String.format("%s/verify?success=false&message=%s", frontendOrigin, java.net.URLEncoder.encode(result.getMessage(), java.nio.charset.StandardCharsets.UTF_8));
            }
            response.sendRedirect(target);
            return ResponseEntity.status(302).body("Redirecting");
        }

        // Modo API: devolver mensaje claro (200 OK para success, 400 para fallo)
        if (!result.isSuccess()) {
            return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN).body(result.getMessage());
        }
        return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(result.getMessage());
    }

}









