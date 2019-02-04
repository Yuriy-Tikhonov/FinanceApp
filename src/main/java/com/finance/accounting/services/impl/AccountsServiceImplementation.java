package com.finance.accounting.services.impl;

import com.finance.accounting.exceptions.AccountOperationNotPermitted;
import com.finance.accounting.models.AccountDetails;
import com.finance.accounting.models.AccountDetailsGrants;
import com.finance.accounting.models.Currency;
import com.finance.accounting.services.api.AccountsService;
import com.finance.accounting.stores.api.AccountsGrantsStore;
import com.finance.accounting.stores.api.AccountsStore;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.List;

@Singleton
public class AccountsServiceImplementation implements AccountsService {

    private final AccountsStore accountsStore;
    private final AccountsGrantsStore accountsGrantsStore;

    @Inject
    public AccountsServiceImplementation(AccountsStore accountsStore, AccountsGrantsStore accountsGrantsStore) {
        this.accountsStore = accountsStore;
        this.accountsGrantsStore = accountsGrantsStore;
    }

    @Override
    public List<AccountDetails> getAccounts(long clientId) {
        return accountsStore.findAll(clientId);
    }

    @Override
    public String createAccount(String iban, String bic, BigDecimal amount, Currency currency, long clientId) {
        if (accountsStore.find(iban) != null) {
            throw new AccountOperationNotPermitted("Account already exist");
        }
        long id = accountsStore.getNextId();
        accountsStore.save(new AccountDetails(id, iban, bic, amount, currency));

        AccountDetailsGrants accountGrants = accountsGrantsStore.find(clientId);
        if(accountGrants == null){
            accountGrants = new AccountDetailsGrants(clientId);
            accountsGrantsStore.save(accountGrants);
        }
        accountGrants.addIban(iban);

        return iban;
    }

}
