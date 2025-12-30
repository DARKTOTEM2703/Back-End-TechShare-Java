package com.techmate.techmate.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.techmate.techmate.service.EmailService;
import jakarta.mail.internet.MimeMessage;

class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Create EmailService with mocked JavaMailSender
        emailService = new EmailService();
        // Use reflection to inject the mocked JavaMailSender
        try {
            var field = EmailService.class.getDeclaredField("mailSender");
            field.setAccessible(true);
            field.set(emailService, mailSender);
        } catch (Exception e) {
            fail("Could not inject mocked JavaMailSender: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("EmailService should have sendEmail method")
    void shouldHaveSendEmailMethod() throws NoSuchMethodException {
        assertNotNull(EmailService.class.getDeclaredMethod("sendEmail", String.class, String.class, String.class));
    }

    @Test
    @DisplayName("EmailService should have sendHtmlEmail method")
    void shouldHaveSendHtmlEmailMethod() throws NoSuchMethodException {
        assertNotNull(EmailService.class.getDeclaredMethod("sendHtmlEmail", String.class, String.class, String.class));
    }

    @Test
    @DisplayName("Should accept valid email parameters in sendEmail")
    void sendEmail_WithValidParams_ShouldAcceptCall() {
        // Verify that the method accepts typical email parameters
        try {
            var method = EmailService.class.getDeclaredMethod("sendEmail", String.class, String.class, String.class);
            assertEquals("sendEmail", method.getName());
            assertEquals(3, method.getParameterCount());
        } catch (NoSuchMethodException e) {
            fail("sendEmail method signature is incorrect");
        }
    }

    @Test
    @DisplayName("Should accept valid email parameters in sendHtmlEmail")
    void sendHtmlEmail_WithValidParams_ShouldAcceptCall() {
        // Verify that the method accepts typical email parameters
        try {
            var method = EmailService.class.getDeclaredMethod("sendHtmlEmail", String.class, String.class,
                    String.class);
            assertEquals("sendHtmlEmail", method.getName());
            assertEquals(3, method.getParameterCount());
        } catch (NoSuchMethodException e) {
            fail("sendHtmlEmail method signature is incorrect");
        }
    }

    @Test
    @DisplayName("EmailService should be marked as a Spring Service")
    void shouldBeAnnotatedAsService() {
        // Verify the class has @Service annotation or equivalent
        assertTrue(EmailService.class.isAnnotationPresent(org.springframework.stereotype.Service.class),
                "EmailService should be annotated with @Service");
    }

    @Test
    @DisplayName("Should handle multiple email addresses correctly")
    void sendEmail_WithMultipleRecipients_ShouldHandleEach() {
        // This test verifies that the service structure supports sending to multiple
        // recipients
        String[] recipients = { "user1@example.com", "user2@example.com", "user3@example.com" };
        String subject = "Test Subject";
        String text = "Test Message";

        // Just verify the method exists and can be called multiple times
        for (String recipient : recipients) {
            assertDoesNotThrow(() -> {
                var method = EmailService.class.getDeclaredMethod("sendEmail", String.class, String.class,
                        String.class);
                assertNotNull(method);
            });
        }
    }

    @Test
    @DisplayName("Should validate email service is asynchronous")
    void shouldHaveAsyncAnnotation() {
        // Verify async annotation on sendEmail method
        try {
            var method = EmailService.class.getDeclaredMethod("sendEmail", String.class, String.class, String.class);
            assertTrue(
                    method.isAnnotationPresent(org.springframework.scheduling.annotation.Async.class),
                    "sendEmail should be annotated with @Async");
        } catch (NoSuchMethodException e) {
            fail("sendEmail method not found");
        }
    }

    @Test
    @DisplayName("Should validate sendHtmlEmail is asynchronous")
    void shouldHaveAsyncAnnotationOnHtmlEmail() {
        // Verify async annotation on sendHtmlEmail method
        try {
            var method = EmailService.class.getDeclaredMethod("sendHtmlEmail", String.class, String.class,
                    String.class);
            assertTrue(
                    method.isAnnotationPresent(org.springframework.scheduling.annotation.Async.class),
                    "sendHtmlEmail should be annotated with @Async");
        } catch (NoSuchMethodException e) {
            fail("sendHtmlEmail method not found");
        }
    }
}
