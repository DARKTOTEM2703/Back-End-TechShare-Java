package com.techmate.techmate.infrastructure.service.notification.impl;

import com.techmate.techmate.infrastructure.service.notification.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementación mock del servicio de emails.
 * En producción, se reemplazaría con una implementación real (SMTP, SendGrid,
 * AWS SES, etc.).
 * 
 * Esta implementación:
 * - Registra los emails en logs
 * - No envía correos reales
 * - Útil para desarrollo y testing
 */
@Slf4j
@Service
public class MockEmailService implements EmailService {

    @Override
    public void sendBorrowConfirmation(String userEmail, Integer borrowId, String materials,
            java.math.BigDecimal amount) {
        log.info(
                "📧 [MOCK EMAIL] Confirmación de préstamo enviada a {}\n" +
                        "   Préstamo ID: {}\n" +
                        "   Materiales: {}\n" +
                        "   Monto: ${}",
                userEmail, borrowId, materials,
                amount != null ? amount.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString() : "0.00");

        // En producción:
        // emailClient.send(userEmail, "Confirmación de Préstamo", template.render(...))
    }

    @Override
    public void sendReturnConfirmation(String userEmail, Integer borrowId, String returnDate) {
        log.info(
                "📧 [MOCK EMAIL] Confirmación de devolución enviada a {}\n" +
                        "   Préstamo ID: {}\n" +
                        "   Fecha de devolución: {}",
                userEmail, borrowId, returnDate);
    }

    @Override
    public void sendLateReturnAlert(String userEmail, Integer borrowId, Long daysLate, java.math.BigDecimal penalty) {
        log.warn(
                "📧 [MOCK EMAIL] Alerta de devolución tardía enviada a {}\n" +
                        "   Préstamo ID: {}\n" +
                        "   Días de retraso: {}\n" +
                        "   Penalización: ${}",
                userEmail, borrowId, daysLate,
                penalty != null ? penalty.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString() : "0.00");
    }

    @Override
    public void sendLowStockAlert(String adminEmail, String materialName, Integer currentStock, Integer threshold) {
        log.warn(
                "📧 [MOCK EMAIL] Alerta de stock bajo enviada a {}\n" +
                        "   Material: {}\n" +
                        "   Stock actual: {}\n" +
                        "   Umbral: {}",
                adminEmail, materialName, currentStock, threshold);
    }
}







