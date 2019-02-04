package com.finance.accounting.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public final class Account {

    private final String iban;
    private final String bic;

    @JsonCreator
    public Account(@JsonProperty("iban") String iban, @JsonProperty("bic") String bic) {
        this.iban = iban;
        this.bic = bic;
    }

    public String getIban() {
        return iban;
    }

    public String getBic() {
        return bic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(iban, account.iban) &&
               Objects.equals(bic, account.bic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(iban, bic);
    }
}
