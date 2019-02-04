package com.finance.accounting.services.impl;

import com.finance.accounting.models.AccountDetails;
import com.finance.accounting.services.api.OrderProcessingService;

import javax.inject.Singleton;
import java.math.BigDecimal;

@Singleton
public class OrderProcessingImplementation implements OrderProcessingService {

    @Override
    public BigDecimal transferAmount(
                AccountDetails sourceAccount,
                AccountDetails destAccount,
                BigDecimal amount) {

        AccountDetails[] locks = {sourceAccount, destAccount};
        if (destAccount.getId() < sourceAccount.getId()) {
            locks[0] = destAccount;
            locks[1] = sourceAccount;
        }
        synchronized (locks[0]) {
            synchronized (locks[1]) {
                creditAccount(destAccount, amount);
                return debitAccount(sourceAccount, amount);
            }
        }
    }

    @Override
    public BigDecimal chargeAmount(AccountDetails sourceAccount, BigDecimal amount) {
        synchronized (sourceAccount) {
            return debitAccount(sourceAccount, amount);
        }
    }

    private BigDecimal creditAccount(AccountDetails destAccount, BigDecimal amount) {
        BigDecimal newDestAmount = destAccount.getAmount().add(amount);
        destAccount.setAmount(newDestAmount);
        return newDestAmount;
    }

    private BigDecimal debitAccount(AccountDetails sourceAccount, BigDecimal amount) {
        BigDecimal newSourceAmount = sourceAccount.getAmount().subtract(amount);
        sourceAccount.setAmount(newSourceAmount);
        return newSourceAmount;
    }

}
