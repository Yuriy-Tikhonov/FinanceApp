package com.finance.accounting.guice;

import com.finance.accounting.services.api.TransfersService;
import com.finance.accounting.services.impl.TransfersServiceImplementation;
import com.google.inject.AbstractModule;

public class TransfersServiceModule extends AbstractModule {
    protected void configure() {
        bind(TransfersService.class).to(TransfersServiceImplementation.class);
    }
}
