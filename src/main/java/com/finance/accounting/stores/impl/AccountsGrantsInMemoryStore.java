package com.finance.accounting.stores.impl;

import com.finance.accounting.models.AccountDetailsGrants;
import com.finance.accounting.stores.api.AccountsGrantsStore;

import javax.inject.Singleton;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public final class AccountsGrantsInMemoryStore implements AccountsGrantsStore {

    private static final ConcurrentHashMap<Long, AccountDetailsGrants> store = new ConcurrentHashMap<>();

    private static void initFromResourceFile() throws IOException {
        String rootPath = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("")).getPath();
        String appConfigPath = rootPath + "application.properties";

        Properties appProps = new Properties();
        appProps.load(new FileInputStream(appConfigPath));
        String fileName = appProps.getProperty("testData.accountsGrants");

        try (BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName))))) {
            Scanner io = new Scanner(br);
            while (io.hasNext()) {
                long clientId = io.nextLong();
                String iban = io.next();
                AccountDetailsGrants storeId = store.get(clientId);
                if(storeId != null) {
                    storeId.addIban(iban);
                } else {
                    AccountDetailsGrants first = new AccountDetailsGrants(clientId, iban);
                    store.put(first.getClientId(), first);
                }
            }
        }
    }

    public AccountsGrantsInMemoryStore() throws IOException {
        initFromResourceFile();
    }

    @Override
    public void save(AccountDetailsGrants account) {
        store.put(account.getClientId(), account);
    }

    @Override
    public AccountDetailsGrants find(long clientId) {
        return store.get(clientId);
    }

    @Override
    public void clear() throws IOException {
        store.clear();
        initFromResourceFile();
    }
}
