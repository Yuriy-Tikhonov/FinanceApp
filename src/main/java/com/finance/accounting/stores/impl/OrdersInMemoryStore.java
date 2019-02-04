package com.finance.accounting.stores.impl;

import com.finance.accounting.models.Transfer;
import com.finance.accounting.stores.api.OrdersStore;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Singleton
public final class OrdersInMemoryStore implements OrdersStore {

    private static final ConcurrentHashMap<Long, Transfer> store = new ConcurrentHashMap<>();

    private static final AtomicLong nextId = new AtomicLong(1L);

    public OrdersInMemoryStore() {
    }

    @Override
    public long getNextId() {
        return nextId.getAndIncrement();
    }

    @Override
    public void save(Transfer transfer) {
        store.put(transfer.getId(), transfer);
    }

    @Override
    public synchronized List<Transfer> findAll() {
        List<Transfer> accounts = new ArrayList<>(store.values());
        return Collections.unmodifiableList(accounts);
    }

    @Override
    public void clear(){
        store.clear();
        nextId.set(1L);
    }
}
