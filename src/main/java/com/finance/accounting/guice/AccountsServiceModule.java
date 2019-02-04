package com.finance.accounting.guice;

import com.finance.accounting.services.api.AccountsService;
import com.finance.accounting.services.impl.AccountsServiceImplementation;
import com.google.inject.AbstractModule;

public class AccountsServiceModule extends AbstractModule {
    protected void configure() {
        bind(AccountsService.class).to(AccountsServiceImplementation.class);
    }
}
