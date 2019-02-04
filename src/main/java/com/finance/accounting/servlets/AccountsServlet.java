package com.finance.accounting.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finance.accounting.exceptions.AccountOperationNotPermitted;
import com.finance.accounting.models.AccountDetails;
import com.finance.accounting.requests.CreateAccountRequest;
import com.finance.accounting.requests.GetAccountRequest;
import com.finance.accounting.responses.HttpFinResponse;
import com.finance.accounting.services.api.AccountsService;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Singleton
public class AccountsServlet extends HttpServlet {

    private final AccountsService accountsService;

    private static final String CONTENT_TYPE = "application/json";

    @Inject
    public AccountsServlet(AccountsService accountsService) {
        this.accountsService = accountsService;
    }

    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {
        response.setContentType(CONTENT_TYPE);
        response.setStatus(HttpServletResponse.SC_OK);
        ObjectMapper objectMapper = new ObjectMapper();
        GetAccountRequest getAccountRequest = new GetAccountRequest(Long.parseLong(request.getParameter("clientId")));
        List<AccountDetails> accounts = accountsService.getAccounts(getAccountRequest.getClientId());
        HttpFinResponse<List<AccountDetails>> finResponse = new HttpFinResponse<>(HttpFinResponse.Status.SUCCESS, accounts);
        response.getWriter().println(objectMapper.writeValueAsString(finResponse));
    }

    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {
        response.setContentType(CONTENT_TYPE);
        response.setStatus(HttpServletResponse.SC_OK);
        ObjectMapper objectMapper = new ObjectMapper();
        CreateAccountRequest createAccountRequest = objectMapper.readValue(request.getReader(), CreateAccountRequest.class);

        try {
            String iban = accountsService.createAccount(
                    createAccountRequest.getIban(),
                    createAccountRequest.getBic(),
                    createAccountRequest.getAmount(),
                    createAccountRequest.getCurrency(),
                    createAccountRequest.getClientId());
            HttpFinResponse<String> finResponse = new HttpFinResponse<>(HttpFinResponse.Status.SUCCESS, iban);
            response.getWriter().println(objectMapper.writeValueAsString(finResponse));
        } catch (AccountOperationNotPermitted e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            HttpFinResponse<String> finResponse = new HttpFinResponse<>(HttpFinResponse.Status.ERROR, e.getMessage());
            response.getWriter().println(objectMapper.writeValueAsString(finResponse));
        }
    }
}
