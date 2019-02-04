package com.finance.accounting.stores.api;

import com.finance.accounting.models.Transfer;

import java.util.List;

public interface OrdersStore {
    long getNextId();
    void save(Transfer transfer);
    List<Transfer> findAll();
    void clear();
}
