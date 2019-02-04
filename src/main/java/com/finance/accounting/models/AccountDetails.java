package com.finance.accounting.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public final class AccountDetails {

    private final long id;

    private final Account account;

    private BigDecimal amount;
    private final Currency currency;

    @JsonCreator
    public AccountDetails(
            @JsonProperty("id") long id,
            @JsonProperty("iban") String iban,
            @JsonProperty("bic") String bic,
            @JsonProperty("amount") BigDecimal amount,
            @JsonProperty("currency") Currency currency) {
        this.id = id;
        this.account = new Account(iban, bic);
        this.amount = amount;
        this.currency = currency;
    }

    public long getId() {
        return id;
    }

    public Account getAccount() {
        return account;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AccountDetails that = (AccountDetails) o;

        if (id != that.id) return false;
        if (!account.equals(that.account)) return false;
        if (amount.compareTo(that.amount)!=0) return false;
        return currency == that.currency;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + account.hashCode();
        result = 31 * result + amount.hashCode();
        result = 31 * result + currency.hashCode();
        return result;
    }
}
