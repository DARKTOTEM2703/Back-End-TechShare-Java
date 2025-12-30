package com.techmate.techmate.hexagonal.ui.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.techmate.techmate.hexagonal.infrastructure.dto.UsuarioDTO;
import com.techmate.techmate.hexagonal.infrastructure.service.UserService;

@RestController
@RequestMapping("/admin/user")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Obtener usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> getUserById(@PathVariable Integer id) {
        return userService.findUserById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario con ID " + id + " no encontrado"));
    }

    @GetMapping("/all")
    public ResponseEntity<List<UsuarioDTO>> getAllUser() {
        List<UsuarioDTO> usuarios = userService.getAllUser();
        return ResponseEntity.ok(usuarios);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> updateUser(@PathVariable Integer id, @RequestBody UsuarioDTO usuarioDTO) {
        return userService.updateUser(id, usuarioDTO)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario con ID " + id + " no encontrado"));
    }
}







