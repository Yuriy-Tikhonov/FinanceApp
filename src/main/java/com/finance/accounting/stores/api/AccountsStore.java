package com.finance.accounting.stores.api;

import com.finance.accounting.models.AccountDetails;

import java.io.IOException;
import java.util.List;

public interface AccountsStore {
    long getNextId();
    void save(AccountDetails account);
    AccountDetails find(String iban);
    List<AccountDetails> findAll(long clientId);
    void clear() throws IOException;
}
