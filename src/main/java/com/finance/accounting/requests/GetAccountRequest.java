package com.finance.accounting.requests;

public class GetAccountRequest {

    private final long clientId;

    public GetAccountRequest(long clientId) {
        this.clientId = clientId;
    }

    public long getClientId() {
        return clientId;
    }
}
