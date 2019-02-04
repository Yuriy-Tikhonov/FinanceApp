package com.finance.accounting.services.api;

import com.finance.accounting.models.Currency;
import com.finance.accounting.models.Transfer;

import java.math.BigDecimal;
import java.util.List;

public interface TransfersService {
    List<Transfer> getTransfers();

    long createTransfer(BigDecimal amount,
                        Currency currency,
                        String senderIban,
                        String recipientIban,
                        String notes,
                        long clientId);
}
