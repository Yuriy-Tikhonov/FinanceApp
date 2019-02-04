package com.finance.accounting.requests;

import com.finance.accounting.models.Currency;

import java.math.BigDecimal;

public class CreateAccountRequest {

    private String iban;
    private String bic;

    private BigDecimal amount;
    private Currency currency;

    private long clientId;

    public String getIban() {
        return iban;
    }
    public String getBic() {
        return bic;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }
}
