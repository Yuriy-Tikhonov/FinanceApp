package com.finance.accounting.services.impl;

import com.finance.accounting.exceptions.AccountOperationNotPermitted;
import com.finance.accounting.models.AccountDetails;
import com.finance.accounting.models.Currency;
import com.finance.accounting.models.OrderStatus;
import com.finance.accounting.models.Transfer;
import com.finance.accounting.services.api.OrderProcessingService;
import com.finance.accounting.services.api.TransfersService;
import com.finance.accounting.stores.api.AccountsStore;
import com.finance.accounting.stores.api.OrdersStore;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.List;

@Singleton
public class TransfersServiceImplementation implements TransfersService {

    private final AccountsStore accountsStore;
    private final OrdersStore transfersStore;
    private final OrderProcessingService orderProcessingService;

    @Inject
    public TransfersServiceImplementation(
            OrdersStore transfersStore,
            AccountsStore accountsStore,
            OrderProcessingService orderProcessingService){
        this.transfersStore = transfersStore;
        this.accountsStore = accountsStore;
        this.orderProcessingService = orderProcessingService;
    }

    @Override
    public List<Transfer> getTransfers() {
        return transfersStore.findAll();
    }

    @Override
    public long createTransfer(
            BigDecimal amount,
            Currency currency,
            String senderIban,
            String recipientIban,
            String notes,
            long clientId) {
        long id = transfersStore.getNextId();
        Transfer newOrder = new Transfer(id, amount, currency, senderIban, recipientIban, notes, clientId);
        transfersStore.save(newOrder);
/*
The creation process should complete here.

The order must be processed by another process,
which might be called asynchronously or through a message broker.

Since this is a standalone application, I make an explicit call to the handler here.
*/

        processTransfer(newOrder);

        return id;
    }

    private OrderStatus processTransfer(Transfer order) {
/*
It is not the best place for this method.
It should be in processing service.
There is should be some kind of strategy class for processing of different types of transfers.
*/
        order.setCurrentStatus(OrderStatus.PROCESSING); // It is better to implement by using a state machine

        AccountDetails destAccount = accountsStore.find(order.getRecipientIban());
        AccountDetails sourceAccount = accountsStore.find(order.getSenderIban());

        if ((sourceAccount != null) && sourceAccount.equals(destAccount)) {
            throw new AccountOperationNotPermitted("Source and dest accounts the same");
        }

        if (sourceAccount != null) {
            if (destAccount != null) {
                // Inner Transaction
                orderProcessingService.transferAmount(sourceAccount, destAccount, order.getAmount());
                order.setCurrentStatus(OrderStatus.SUCCESS);
            } else {
                // Outer Transaction
                orderProcessingService.chargeAmount(sourceAccount, order.getAmount());
                order.setCurrentStatus(OrderStatus.SUCCESS);
            }
        } else {
            // Cancel Transaction
            order.setCurrentStatus(OrderStatus.FAILURE);
        }

        return order.getCurrentStatus();
    }
}
