package com.finance.accounting.services.api;

import com.finance.accounting.models.AccountDetails;
import com.finance.accounting.models.Currency;

import java.math.BigDecimal;
import java.util.List;

public interface AccountsService {
    List<AccountDetails> getAccounts(long clientId);
    String createAccount(String iban, String bic, BigDecimal amount, Currency currency, long clientId);
}
