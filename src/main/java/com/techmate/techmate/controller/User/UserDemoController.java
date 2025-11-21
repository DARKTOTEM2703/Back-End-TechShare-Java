package com.techmate.techmate.controller.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techmate.techmate.service.TokenService;
import com.techmate.techmate.service.User.UserDemoService;
import com.techmate.techmate.dto.UsuarioDTO;

@RequestMapping("/user")
@RestController
public class UserDemoController {
    

    private final TokenService tokenService;

    private final UserDemoService userDemoService;

    public UserDemoController(TokenService tokenService, UserDemoService userDemoService) {
        this.tokenService = tokenService;
        this.userDemoService = userDemoService;
    }

    // Endpoint para obtener la información del usuario desde el token
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> obtenerUsuarioInfo(@RequestHeader("Authorization") String token) {
        // Eliminar "Bearer " del token
        String cleanToken = token.replace("Bearer ", "");
        
        // Integer userId = tokenService.getUserIdFromToken(cleanToken);
        //String email = tokenService.getUserEmailFromToken(cleanToken);
        String userName = tokenService.getUserNameFromToken(cleanToken);  // Obtener el nombre de usuario
        //var roles = tokenService.getRolesFromToken(cleanToken).orElse(List.of());

        // Construir respuesta
        Map<String, Object> usuarioInfo = new HashMap<>();
       // usuarioInfo.put("userId", userId);
       // usuarioInfo.put("email", email);
        usuarioInfo.put("userName", userName);  // Incluir el nombre de usuario en la respuesta
        //usuarioInfo.put("roles", roles);

        return ResponseEntity.ok(usuarioInfo);
    }
    // Endpoint para obtener los detalles completos del usuario usando el token

    @GetMapping("/details")
    public ResponseEntity<?> obtenerUsuarioDetalles(@RequestHeader("Authorization") String token) {
        // Eliminar posibles espacios en blanco y "Bearer "
        String cleanToken = token.replace("Bearer ", "").trim();
        System.out.println("Token limpio: " + cleanToken); // Verificar token limpio
    
        Optional<UsuarioDTO> usuario = userDemoService.getUserDetailsFromToken(cleanToken);
    
        if (usuario.isPresent()) {
            return ResponseEntity.ok(usuario.get());
        } else {
            System.out.println("Usuario no encontrado: " + cleanToken); // Verificación de que el usuario existe
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }
    }
    
    
}


