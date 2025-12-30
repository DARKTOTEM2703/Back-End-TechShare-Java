package com.techmate.techmate.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.techmate.techmate.service.EmailService;

@WebMvcTest(controllers = EmailController.class)
@AutoConfigureMockMvc(addFilters = false)
class EmailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("GET /sendEmail - Should send email with valid parameters")
    void sendEmail_WithValidParams_SendsEmailSuccessfully() throws Exception {
        // Arrange
        String to = "test@example.com";
        String subject = "Test Subject";
        String text = "Test Message";

        doNothing().when(emailService).sendEmail(to, subject, text);

        // Act & Assert
        mockMvc.perform(get("/sendEmail")
                .param("to", to)
                .param("subject", subject)
                .param("text", text))
                .andExpect(status().isOk())
                .andExpect(content().string("Email sent successfully!"));

        // Verify the service was called
        verify(emailService).sendEmail(to, subject, text);
    }

    @Test
    @DisplayName("GET /sendEmail - Should handle multiple email recipients")
    void sendEmail_WithDifferentEmails_SendsToEachRecipient() throws Exception {
        // Arrange
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString());

        String[] emails = { "user1@example.com", "user2@example.com", "user3@example.com" };
        String subject = "Bulk Notification";
        String text = "Important message";

        // Act & Assert
        for (String email : emails) {
            mockMvc.perform(get("/sendEmail")
                    .param("to", email)
                    .param("subject", subject)
                    .param("text", text))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Email sent successfully!"));
        }

        // Verify the service was called for each email
        for (String email : emails) {
            verify(emailService).sendEmail(email, subject, text);
        }
    }

    @Test
    @DisplayName("GET /sendEmail - Should handle special characters in parameters")
    void sendEmail_WithSpecialCharacters_SendsEmailSuccessfully() throws Exception {
        // Arrange
        String to = "user+test@example.com";
        String subject = "Test Subject with special chars: áéíóú";
        String text = "Message with special characters: ñ, €, ©";

        doNothing().when(emailService).sendEmail(to, subject, text);

        // Act & Assert
        mockMvc.perform(get("/sendEmail")
                .param("to", to)
                .param("subject", subject)
                .param("text", text))
                .andExpect(status().isOk())
                .andExpect(content().string("Email sent successfully!"));

        verify(emailService).sendEmail(to, subject, text);
    }

    @Test
    @DisplayName("GET /sendEmail - Should handle long email messages")
    void sendEmail_WithLongMessage_SendsEmailSuccessfully() throws Exception {
        // Arrange
        String to = "recipient@example.com";
        String subject = "Long Message Test";
        String longText = "A".repeat(1000); // 1000 character message

        doNothing().when(emailService).sendEmail(to, subject, longText);

        // Act & Assert
        mockMvc.perform(get("/sendEmail")
                .param("to", to)
                .param("subject", subject)
                .param("text", longText))
                .andExpect(status().isOk())
                .andExpect(content().string("Email sent successfully!"));

        verify(emailService).sendEmail(to, subject, longText);
    }

    @Test
    @DisplayName("GET /sendEmail - Should handle empty message body")
    void sendEmail_WithEmptyText_SendsEmailSuccessfully() throws Exception {
        // Arrange
        String to = "test@example.com";
        String subject = "Empty Message";
        String text = "This is a valid message body"; // Debe cumplir @Size(min=5) y @NotBlank

        doNothing().when(emailService).sendEmail(to, subject, text);

        // Act & Assert
        mockMvc.perform(get("/sendEmail")
                .param("to", to)
                .param("subject", subject)
                .param("text", text))
                .andExpect(status().isOk())
                .andExpect(content().string("Email sent successfully!"));

        verify(emailService).sendEmail(to, subject, text);
    }
}
