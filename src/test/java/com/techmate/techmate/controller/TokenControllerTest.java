package com.techmate.techmate.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.techmate.techmate.service.TokenService;

@WebMvcTest(controllers = TokenController.class)
@AutoConfigureMockMvc(addFilters = false)
class TokenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("GET /tokens/userId - Should extract userId from bearer token")
    void getUserIdFromToken_WithBearerToken_ReturnsUserId() throws Exception {
        // Arrange
        Integer expectedUserId = 42;
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.token";
        String bearerToken = "Bearer " + token;

        when(tokenService.getUserIdFromToken(token)).thenReturn(expectedUserId);

        // Act & Assert
        mockMvc.perform(get("/tokens/userId")
                .header("Authorization", bearerToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(expectedUserId));
    }

    @Test
    @DisplayName("GET /tokens/userId - Should extract userId from raw token")
    void getUserIdFromToken_WithRawToken_ReturnsUserId() throws Exception {
        // Arrange
        Integer expectedUserId = 15;
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.rawtoken";

        when(tokenService.getUserIdFromToken(token)).thenReturn(expectedUserId);

        // Act & Assert
        mockMvc.perform(get("/tokens/userId")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(expectedUserId));
    }

    @Test
    @DisplayName("GET /tokens/userId - Should handle null userId from service")
    void getUserIdFromToken_WhenServiceReturnsNull_ReturnsNull() throws Exception {
        // Arrange
        String token = "invalidtoken";

        when(tokenService.getUserIdFromToken(anyString())).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/tokens/userId")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /tokens/userId - Should return different userIds for different tokens")
    void getUserIdFromToken_WithMultipleTokens_ReturnsDifferentUserIds() throws Exception {
        // Arrange
        String token1 = "token1";
        String token2 = "token2";
        Integer userId1 = 10;
        Integer userId2 = 20;

        when(tokenService.getUserIdFromToken(token1)).thenReturn(userId1);
        when(tokenService.getUserIdFromToken(token2)).thenReturn(userId2);

        // Act & Assert - First token
        mockMvc.perform(get("/tokens/userId")
                .header("Authorization", "Bearer " + token1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(userId1));

        // Act & Assert - Second token
        mockMvc.perform(get("/tokens/userId")
                .header("Authorization", "Bearer " + token2)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(userId2));
    }
}
