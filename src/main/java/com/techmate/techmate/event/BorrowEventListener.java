package com.techmate.techmate.event;

import com.techmate.techmate.service.audit.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Event Listener for Borrow-related domain events.
 * Handles notifications and side effects asynchronously.
 * 
 * Desacoplamiento de responsabilidades:
 * - BorrowService: Lógica de préstamos
 * - BorrowEventListener: Notificaciones, auditoría, efectos secundarios
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BorrowEventListener {

    private final AuditService auditService;

    /**
     * Handles BorrowCreatedEvent asynchronously.
     * Actions:
     * - Envía confirmación de préstamo por email
     * - Registra en auditoría
     * - Log de información
     */
    @Async
    @EventListener
    public void handleBorrowCreated(BorrowCreatedEvent event) {
        log.info(
                "✅ BORROW CREATED - User {} created borrow {} with amount ${}. EventID: {}",
                event.getUserId(),
                event.getId(),
                event.getAmount() != null
                        ? event.getAmount().setScale(2, java.math.RoundingMode.HALF_UP).toPlainString()
                        : "0.00",
                event.getEventId());

        try {
            // 📧 Enviar confirmación de préstamo
            // emailService.sendBorrowConfirmation(
            // event.getUserEmail(),
            // event.getId(),
            // event.getMaterialsDescription(),
            // event.getAmount()
            // );

            // 📋 Registrar en auditoría
            auditService.logBorrow(
                    event.getId(),
                    "CREATED",
                    event.getUserId(),
                    "Préstamo creado por usuario. Monto: $" + event.getAmount());
        } catch (Exception e) {
            log.error("Error procesando BorrowCreatedEvent para borrow {}: {}", event.getId(), e.getMessage(), e);
        }
    }

    /**
     * Handles BorrowReturnedEvent asynchronously.
     * Actions:
     * - Verifica si la devolución fue tardía
     * - Envía confirmación o alerta según corresponda
     * - Registra en auditoría
     */
    @Async
    @EventListener
    public void handleBorrowReturned(BorrowReturnedEvent event) {
        try {
            if (event.isWasLate()) {
                long daysLate = calculateDaysLate(event.getEndDate(), event.getReturnDate());

                log.warn(
                        "⚠️ LATE RETURN - Borrow {} was returned {} days late. User: {}. EventID: {}",
                        event.getId(),
                        daysLate,
                        event.getUserId(),
                        event.getEventId());

                // 📧 Enviar alerta de devolución tardía
                // double penalty = calculatePenalty(daysLate);
                // emailService.sendLateReturnAlert(
                // event.getUserEmail(),
                // event.getId(),
                // daysLate,
                // penalty
                // );

                // 📋 Registrar devolución tardía
                auditService.logBorrow(
                        event.getId(),
                        "RETURNED_LATE",
                        event.getUserId(),
                        "Devolución realizada " + daysLate + " días después de la fecha programada (" +
                                event.getEndDate() + ")");
            } else {
                log.info(
                        "✅ BORROW RETURNED ON TIME - Borrow {} returned punctually. User: {}. EventID: {}",
                        event.getId(),
                        event.getUserId(),
                        event.getEventId());

                // 📧 Enviar confirmación de devolución exitosa
                // emailService.sendReturnConfirmation(
                // event.getUserEmail(),
                // event.getId(),
                // event.getReturnDate().toString()
                // );

                // 📋 Registrar devolución exitosa
                auditService.logBorrow(
                        event.getId(),
                        "RETURNED_ON_TIME",
                        event.getUserId(),
                        "Devolución completada a tiempo");
            }
        } catch (Exception e) {
            log.error("Error procesando BorrowReturnedEvent para borrow {}: {}", event.getId(), e.getMessage(), e);
        }
    }

    /**
     * Calcula días de retraso entre fecha programada y fecha de devolución.
     */
    private long calculateDaysLate(java.util.Date estimatedDate, java.util.Date returnDate) {
        if (estimatedDate == null || returnDate == null)
            return 0;
        return (returnDate.getTime() - estimatedDate.getTime()) / (1000 * 60 * 60 * 24);
    }
}
