package com.finance.accounting.guice;

import com.finance.accounting.stores.api.OrdersStore;
import com.finance.accounting.stores.impl.OrdersInMemoryStore;
import com.google.inject.AbstractModule;

public class OrdersStoreModule extends AbstractModule {
    protected void configure() {
        bind(OrdersStore.class).to(OrdersInMemoryStore.class);
    }
}
