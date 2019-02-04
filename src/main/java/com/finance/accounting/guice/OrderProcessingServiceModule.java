package com.finance.accounting.guice;

import com.finance.accounting.services.api.OrderProcessingService;
import com.finance.accounting.services.impl.OrderProcessingImplementation;
import com.google.inject.AbstractModule;

public class OrderProcessingServiceModule extends AbstractModule {
    protected void configure() {
        bind(OrderProcessingService.class).to(OrderProcessingImplementation.class);
    }
}
