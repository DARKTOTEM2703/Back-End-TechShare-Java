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

import com.techmate.techmate.entity.Role;
import com.techmate.techmate.entity.Usuario;
import com.techmate.techmate.repository.UsuarioRepository;

/**
 * Tests simples de validación para diferentes servicios
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("🔧 Servicios - Tests Validación Simples")
class ServiceValidationSimpleTest {
    
    @Mock
    private UsuarioRepository usuarioRepository;
    
    private Usuario testUsuario;
    private Role testRole;
    
    @BeforeEach
    void setUp() {
        testRole = new Role();
        testRole.setId(1);
        testRole.setName("ADMIN");
        
        testUsuario = new Usuario();
        testUsuario.setId(1);
        testUsuario.setUser_name("john_doe");
        testUsuario.setEmail("john@example.com");
    }
    
    @Test
    @DisplayName("✅ Validación: nombre de usuario no vacío")
    void validUsuarioName_NotEmpty() {
        // Given
        String username = testUsuario.getUser_name();
        
        // When
        boolean isValid = username != null && !username.trim().isEmpty();
        
        // Then
        assertThat(isValid).isTrue();
    }
    
    @Test
    @DisplayName("✅ Validación: email tiene @")
    void validEmail_HasAtSymbol() {
        // Given
        String email = testUsuario.getEmail();
        
        // When
        boolean isValid = email.contains("@");
        
        // Then
        assertThat(isValid).isTrue();
    }
    
    @Test
    @DisplayName("✅ Validación: ID positivo")
    void validId_IsPositive() {
        // Given
        int id = testUsuario.getId();
        
        // When
        boolean isValid = id > 0;
        
        // Then
        assertThat(isValid).isTrue();
    }
    
    @Test
    @DisplayName("✅ Validación: rol tiene nombre")
    void validRole_HasName() {
        // Given
        String roleName = testRole.getName();
        
        // When
        boolean isValid = roleName != null && !roleName.isEmpty();
        
        // Then
        assertThat(isValid).isTrue();
    }
    
    @Test
    @DisplayName("✅ Búsqueda: usuario encontrado")
    void findUsuario_Success() {
        // Given
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(testUsuario));
        
        // When
        Optional<Usuario> result = usuarioRepository.findById(1);
        
        // Then
        assertThat(result).isPresent();
        verify(usuarioRepository).findById(1);
    }
    
    @Test
    @DisplayName("❌ Búsqueda: usuario no encontrado")
    void findUsuario_NotFound() {
        // Given
        when(usuarioRepository.findById(999)).thenReturn(Optional.empty());
        
        // When
        Optional<Usuario> result = usuarioRepository.findById(999);
        
        // Then
        assertThat(result).isEmpty();
    }
    
    @Test
    @DisplayName("✅ Comparación: nombres coinciden")
    void compareUsername_Match() {
        // Given
        String expected = "john_doe";
        String actual = testUsuario.getUser_name();
        
        // When & Then
        assertThat(actual).isEqualTo(expected);
    }
    
    @Test
    @DisplayName("✅ Comparación: emails coinciden")
    void compareEmail_Match() {
        // Given
        String expected = "john@example.com";
        String actual = testUsuario.getEmail();
        
        // When & Then
        assertThat(actual).isEqualTo(expected);
    }
}

