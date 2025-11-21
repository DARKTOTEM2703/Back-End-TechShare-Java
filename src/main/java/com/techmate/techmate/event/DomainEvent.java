package com.techmate.techmate.event;

import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

/**
 * Base class for all domain events in the application.
 * Domain events represent something meaningful that happened in the domain.
 * 
 * Benefits:
 * - Decouples business logic from side effects
 * - Makes the system more maintainable and testable
 * - Enables async processing and notifications
 * - Provides audit trail of domain changes
 */
public abstract class DomainEvent extends ApplicationEvent {
    
    private final LocalDateTime occurredOn;
    private final String eventId;
    
    protected DomainEvent(Object source) {
        super(source);
        this.occurredOn = LocalDateTime.now();
        this.eventId = java.util.UUID.randomUUID().toString();
    }
    
    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }
    
    public String getEventId() {
        return eventId;
    }
    
    /**
     * Returns the type of event for logging and routing purposes.
     */
    public abstract String getEventType();
}

