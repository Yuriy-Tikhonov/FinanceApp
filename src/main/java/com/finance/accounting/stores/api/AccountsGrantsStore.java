package com.finance.accounting.stores.api;

import com.finance.accounting.models.AccountDetailsGrants;

import java.io.IOException;

public interface AccountsGrantsStore {
    void save(AccountDetailsGrants account);
    AccountDetailsGrants find(long clientId);
    void clear() throws IOException;
}
