package com.techmate.techmate.event;

import lombok.Getter;

import java.util.Date;

import com.techmate.techmate.entity.Borrow;

/**
 * Event fired when a borrow operation is created.
 * This event can trigger:
 * - Email confirmation to the user
 * - Stock update notifications
 * - Audit logging
 */
@Getter
public class BorrowCreatedEvent extends DomainEvent {

    private final Integer borrowId;
    private final Integer userId;
    private final Date borrowDate;
    private final Date startDate;
    private final Date endDate;
    private final java.math.BigDecimal amount;

    public BorrowCreatedEvent(Borrow borrow) {
        super(borrow);
        this.borrowId = borrow.getId();
        this.userId = borrow.getUsuario() != null ? borrow.getUsuario().getId() : null;
        this.borrowDate = borrow.getDate();
        this.startDate = borrow.getStartDate();
        this.endDate = borrow.getEndDate();
        this.amount = borrow.getAmount();
    }

    // Compatibility method: getId() returns borrowId
    public Integer getId() {
        return this.borrowId;
    }

    @Override
    public String getEventType() {
        return "BORROW_CREATED";
    }

    @Override
    public String toString() {
        return String.format(
                "BorrowCreatedEvent[borrowId=%d, userId=%d, amount=%s, eventId=%s]",
                borrowId, userId, amount != null ? amount.toPlainString() : "0.00", getEventId());
    }
}
