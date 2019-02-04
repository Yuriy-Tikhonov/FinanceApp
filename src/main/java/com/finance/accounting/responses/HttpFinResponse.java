package com.finance.accounting.responses;

public class HttpFinResponse<T> {
    private Status status;

    private T body;

    public HttpFinResponse() {
    }

    public HttpFinResponse(Status status) {
        this.status = status;
    }

    public HttpFinResponse(Status status, T body) {
        this.status = status;
        this.body = body;
    }

    public Status getStatus() {
        return status;
    }

    public T getBody() {
        return body;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public enum Status {
        SUCCESS,
        ERROR
    }
}
