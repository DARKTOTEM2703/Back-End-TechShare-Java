package com.techmate.techmate.domain.model;

import com.techmate.techmate.entity.Status;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Dominio puro: Borrow (Rich Domain Model)
 * No depende de Spring ni JPA.
 */
public class Borrow {

    private Integer id;
    private Date date;
    private Date startDate;
    private Date endDate;
    private Date returnDate;
    private Status status;
    private Integer usuarioId;
    private Integer resourceId;
    private BigDecimal amount;
    private List<com.techmate.techmate.domain.model.DetailsBorrow> details;

    public Borrow() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public List<com.techmate.techmate.domain.model.DetailsBorrow> getDetails() {
        return details;
    }

    public void setDetails(List<com.techmate.techmate.domain.model.DetailsBorrow> details) {
        this.details = details;
    }

    public BigDecimal calculateTotalAmount() {
        if (details == null || details.isEmpty())
            return BigDecimal.ZERO;
        return details.stream()
                .map(d -> d.getTotalPrice() == null ? BigDecimal.ZERO : d.getTotalPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Business rule: allowed transitions
    public void changeStatus(Status newStatus, Integer adminId) {
        Objects.requireNonNull(newStatus, "El nuevo estado no puede ser nulo");

        if (this.status == null) {
            throw new IllegalStateException("Estado actual desconocido");
        }

        // Permitted transitions
        if (this.status == Status.PENDING && newStatus == Status.REJECTED) {
            this.status = newStatus;
            return;
        }

        if (this.status == Status.PENDING && newStatus == Status.BORROWED) {
            this.status = newStatus;
            this.startDate = new Date();
            return;
        }

        if (this.status == Status.BORROWED && newStatus == Status.RETURNED) {
            this.status = newStatus;
            this.returnDate = new Date();
            return;
        }

        throw new IllegalStateException("Transición de estado no permitida: " + this.status + " -> " + newStatus);
    }
}
