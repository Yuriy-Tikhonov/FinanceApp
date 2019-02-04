package com.finance.accounting.models;

import java.util.HashSet;
import java.util.Set;

public final class AccountDetailsGrants {

    private final long clientId;

    private final HashSet<String> ibanList = new HashSet<>();

    public AccountDetailsGrants(long clientId) {
        this.clientId = clientId;
    }

    public AccountDetailsGrants(long clientId, String iban) {
        this.clientId = clientId;
        this.ibanList.add(iban);
    }

    public long getClientId() {
        return clientId;
    }

    public Set<String> getIbanList() {
        return ibanList;
    }

    public boolean addIban(String iban) {
        return ibanList.add(iban);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AccountDetailsGrants that = (AccountDetailsGrants) o;

        if (clientId != that.clientId) return false;
        return ibanList.equals(that.ibanList);
    }

    @Override
    public int hashCode() {
        int result = (int) (clientId ^ (clientId >>> 32));
        result = 31 * result + ibanList.hashCode();
        return result;
    }
}
