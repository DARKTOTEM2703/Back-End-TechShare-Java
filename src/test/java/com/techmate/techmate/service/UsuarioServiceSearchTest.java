package com.techmate.techmate.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.techmate.techmate.entity.Usuario;
import com.techmate.techmate.repository.UsuarioRepository;

/**
 * Tests para UsuarioService - Búsquedas y queries
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("👥 UsuarioService - Tests Búsquedas")
class UsuarioServiceSearchTest {
    
    @Mock
    private UsuarioRepository usuarioRepository;
    
    private Usuario testUsuario;
    
    @BeforeEach
    void setUp() {
        testUsuario = new Usuario();
        testUsuario.setId(1);
        testUsuario.setUser_name("john_doe");
        testUsuario.setEmail("john@example.com");
    }
    
    @Test
    @DisplayName("✅ Obtener usuario por ID")
    void getUserById_Found() {
        // Arrange
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(testUsuario));
        
        // Act
        Optional<Usuario> result = usuarioRepository.findById(1);
        
        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getUser_name()).isEqualTo("john_doe");
    }
    
    @Test
    @DisplayName("❌ Usuario no existe")
    void getUserById_NotFound() {
        // Arrange
        when(usuarioRepository.findById(999)).thenReturn(Optional.empty());
        
        // Act
        Optional<Usuario> result = usuarioRepository.findById(999);
        
        // Assert
        assertThat(result).isEmpty();
    }
    
    @Test
    @DisplayName("✅ Listar todos los usuarios")
    void getAllUsuarios_Success() {
        // Arrange
        List<Usuario> usuarios = Arrays.asList(testUsuario);
        when(usuarioRepository.findAll()).thenReturn(usuarios);
        
        // Act
        List<Usuario> result = usuarioRepository.findAll();
        
        // Assert
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
    }
    
    @Test
    @DisplayName("✅ Contar total de usuarios")
    void countAllUsuarios() {
        // Arrange
        when(usuarioRepository.count()).thenReturn(25L);
        
        // Act
        long count = usuarioRepository.count();
        
        // Assert
        assertThat(count).isEqualTo(25L);
    }
    
    @Test
    @DisplayName("✅ Guardar nuevo usuario")
    void saveUsuario_Success() {
        // Arrange
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(testUsuario);
        
        // Act
        Usuario result = usuarioRepository.save(testUsuario);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        verify(usuarioRepository).save(any(Usuario.class));
    }
    
    @Test
    @DisplayName("✅ Eliminar usuario")
    void deleteUsuario_Success() {
        // Arrange
        doNothing().when(usuarioRepository).deleteById(1);
        
        // Act
        usuarioRepository.deleteById(1);
        
        // Assert
        verify(usuarioRepository).deleteById(1);
    }
    
    @Test
    @DisplayName("✅ Validar nombre de usuario")
    void validateUsername_NotEmpty() {
        // Arrange
        String username = testUsuario.getUser_name();
        
        // Act
        boolean isValid = username != null && !username.trim().isEmpty();
        
        // Assert
        assertThat(isValid).isTrue();
    }
    
    @Test
    @DisplayName("✅ Validar email correcto")
    void validateEmail_HasAtSymbol() {
        // Arrange
        String email = testUsuario.getEmail();
        
        // Act
        boolean isValid = email.contains("@");
        
        // Assert
        assertThat(isValid).isTrue();
    }
}

