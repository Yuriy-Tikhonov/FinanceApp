package com.finance.accounting.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public final class Transfer {

    private final long id;

    private final BigDecimal amount;
    private final Currency currency;

    private final String senderIban;
    private final String recipientIban;

    private final String notes;

    private final long createdBy;

    private final LocalDateTime createdAt = LocalDateTime.now();

    private OrderStatus currentStatus = OrderStatus.NEW;
    private LocalDateTime lastStatusChange = LocalDateTime.now();

    @JsonCreator
    public Transfer(
            @JsonProperty("id") long id,
            @JsonProperty("amount")  BigDecimal amount,
            @JsonProperty("currency") Currency currency,
            @JsonProperty("senderIban") String senderIban,
            @JsonProperty("recipientIban") String recipientIban,
            @JsonProperty("notes") String notes,
            @JsonProperty("createdBy") long createdBy) {
        this.id = id;
        this.amount = amount;
        this.currency = currency;
        this.senderIban = senderIban;
        this.recipientIban = recipientIban;
        this.notes = notes;
        this.createdBy = createdBy;
    }

    public long getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public String getSenderIban() {
        return senderIban;
    }

    public String getRecipientIban() {
        return recipientIban;
    }

    public String getNotes() {
        return notes;
    }

    public long getCreatedBy() {
        return createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public OrderStatus getCurrentStatus() {
        return currentStatus;
    }

    public LocalDateTime getLastStatusChange() {
        return lastStatusChange;
    }

    public void setCurrentStatus(OrderStatus currentStatus) {
        this.currentStatus = currentStatus;
        this.lastStatusChange = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Transfer transfer = (Transfer) o;

        if (id != transfer.id) return false;
        if (createdBy != transfer.createdBy) return false;
        if (!amount.equals(transfer.amount)) return false;
        if (currency != transfer.currency) return false;
        if (!senderIban.equals(transfer.senderIban)) return false;
        if (!recipientIban.equals(transfer.recipientIban)) return false;
        if (!Objects.equals(notes, transfer.notes)) return false;
        if (!createdAt.equals(transfer.createdAt)) return false;
        if (currentStatus != transfer.currentStatus) return false;
        return lastStatusChange.equals(transfer.lastStatusChange);
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + amount.hashCode();
        result = 31 * result + currency.hashCode();
        result = 31 * result + senderIban.hashCode();
        result = 31 * result + recipientIban.hashCode();
        result = 31 * result + (notes != null ? notes.hashCode() : 0);
        result = 31 * result + (int) (createdBy ^ (createdBy >>> 32));
        result = 31 * result + createdAt.hashCode();
        result = 31 * result + currentStatus.hashCode();
        result = 31 * result + lastStatusChange.hashCode();
        return result;
    }
}
