package com.finance.accounting.guice;

import com.finance.accounting.stores.api.AccountsStore;
import com.finance.accounting.stores.impl.AccountsInMemoryStore;
import com.google.inject.AbstractModule;

public class AccountsStoreModule extends AbstractModule {
    protected void configure() {
        bind(AccountsStore.class).to(AccountsInMemoryStore.class);
    }
}
