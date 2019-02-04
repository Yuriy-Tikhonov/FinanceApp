package com.finance.accounting.guice;

import com.finance.accounting.stores.api.AccountsGrantsStore;
import com.finance.accounting.stores.impl.AccountsGrantsInMemoryStore;
import com.google.inject.AbstractModule;

public class AccountGrantsStoreModule extends AbstractModule {
    protected void configure() {
        bind(AccountsGrantsStore.class).to(AccountsGrantsInMemoryStore.class);
    }
}
