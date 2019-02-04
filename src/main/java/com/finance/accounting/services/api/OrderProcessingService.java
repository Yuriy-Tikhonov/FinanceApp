package com.finance.accounting.services.api;

import com.finance.accounting.models.AccountDetails;

import java.math.BigDecimal;

public interface OrderProcessingService {
    BigDecimal transferAmount(AccountDetails sourceAccount, AccountDetails destAccount, BigDecimal amount);
    BigDecimal chargeAmount(AccountDetails sourceAccount, BigDecimal amount);
}
