package com.techmate.techmate.hexagonal.infrastructure.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @org.springframework.scheduling.annotation.Async("eventExecutor")
    public void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (MailException e) {
            // Spring's MailException covers all mail-related errors
            org.slf4j.LoggerFactory.getLogger(EmailService.class).error("Error sending plain email to {}", to, e);
        } catch (IllegalArgumentException e) {
            // Catch parameter validation errors (invalid email, etc.)
            org.slf4j.LoggerFactory.getLogger(EmailService.class).error("Invalid email parameter: {}", to, e);
        }
    }

    @org.springframework.scheduling.annotation.Async("eventExecutor")
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true = HTML

            mailSender.send(message);
        } catch (MessagingException me) {
            // MIME message construction or sending failed
            org.slf4j.LoggerFactory.getLogger(EmailService.class).error("MessagingException sending HTML email to {}",
                    to, me);
        } catch (MailException me) {
            // Spring's MailException for actual sending failures
            org.slf4j.LoggerFactory.getLogger(EmailService.class).error("MailException sending HTML email to {}", to,
                    me);
        } catch (IllegalArgumentException e) {
            // Parameter validation errors
            org.slf4j.LoggerFactory.getLogger(EmailService.class).error("Invalid email parameter: {}", to, e);
        }
    }
}






