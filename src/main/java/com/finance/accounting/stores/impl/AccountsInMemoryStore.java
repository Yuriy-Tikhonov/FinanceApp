package com.finance.accounting.stores.impl;

import com.finance.accounting.models.AccountDetails;
import com.finance.accounting.models.AccountDetailsGrants;
import com.finance.accounting.models.Currency;
import com.finance.accounting.stores.api.AccountsGrantsStore;
import com.finance.accounting.stores.api.AccountsStore;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Singleton
public final class AccountsInMemoryStore implements AccountsStore {

    private static final ConcurrentHashMap<String, AccountDetails> store = new ConcurrentHashMap<>();
    private final AccountsGrantsStore accountsGrantsStore;

    private static final AtomicLong nextId = new AtomicLong(1L);

    private static void initFromResourceFile() throws IOException {
        String rootPath = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("")).getPath();
        String appConfigPath = rootPath + "application.properties";

        Properties appProps = new Properties();
        appProps.load(new FileInputStream(appConfigPath));
        String fileName = appProps.getProperty("testData.accounts");

        try (BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName))))) {
            Scanner io = new Scanner(br);
            while (io.hasNext()) {
                String iban = io.next();
                String bic = io.next();
                BigDecimal amount = io.nextBigDecimal();
                Currency currency = Currency.valueOf(io.next());

                AccountDetails first = new AccountDetails(
                        nextId.getAndIncrement(),
                        iban,
                        bic,
                        amount,
                        currency);

                store.put(first.getAccount().getIban(), first);
            }
        }
    }

    @Inject
    public AccountsInMemoryStore(AccountsGrantsStore accountsGrantsStore) throws IOException {
        initFromResourceFile();
        this.accountsGrantsStore = accountsGrantsStore;
    }

    @Override
    public long getNextId() {
        return nextId.getAndIncrement();
    }

    @Override
    public void save(AccountDetails account) {
        store.put(account.getAccount().getIban(), account);
    }

    @Override
    public AccountDetails find(String iban) {
        return store.get(iban);
    }

    @Override
    public synchronized List<AccountDetails> findAll(long clientId) {
        AccountDetailsGrants accountGrants = accountsGrantsStore.find(clientId);
        if(accountGrants == null){
            return new ArrayList<>();
        }

        ArrayList<AccountDetails> result = new ArrayList<>();
        for(String iban : accountGrants.getIbanList()){
            result.add(store.get(iban));
        }

        return result;
    }

    @Override
    public void clear() throws IOException {
        store.clear();
        nextId.set(1L);
        initFromResourceFile();
    }
}
