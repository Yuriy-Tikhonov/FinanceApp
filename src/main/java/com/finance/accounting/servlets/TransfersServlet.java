package com.finance.accounting.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.finance.accounting.exceptions.AccountOperationNotPermitted;
import com.finance.accounting.models.Transfer;
import com.finance.accounting.requests.CreateTransferRequest;
import com.finance.accounting.responses.HttpFinResponse;
import com.finance.accounting.services.api.TransfersService;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Singleton
public class TransfersServlet extends HttpServlet {

    private final TransfersService transfersService;

    private static final String CONTENT_TYPE = "application/json";

    @Inject
    public TransfersServlet(TransfersService transfersService) {
        this.transfersService = transfersService;
    }

    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {
        response.setContentType(CONTENT_TYPE);
        response.setStatus(HttpServletResponse.SC_OK);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        List<Transfer> transfers = transfersService.getTransfers();
        HttpFinResponse<List<Transfer>> finResponse = new HttpFinResponse<>(HttpFinResponse.Status.SUCCESS, transfers);
        response.getWriter().println(objectMapper.writeValueAsString(finResponse));
    }

    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {
        response.setContentType(CONTENT_TYPE);
        response.setStatus(HttpServletResponse.SC_OK);
        ObjectMapper objectMapper = new ObjectMapper();
        CreateTransferRequest createTransferRequest = objectMapper.readValue(request.getReader(), CreateTransferRequest.class);

        try {
            long transferId = transfersService.createTransfer(
                    createTransferRequest.getAmount(),
                    createTransferRequest.getCurrency(),
                    createTransferRequest.getSenderIban(),
                    createTransferRequest.getRecipientIban(),
                    createTransferRequest.getNotes(),
                    createTransferRequest.getCreatedBy());
            HttpFinResponse<Long> finResponse = new HttpFinResponse<>(HttpFinResponse.Status.SUCCESS, transferId);
            response.getWriter().println(objectMapper.writeValueAsString(finResponse));
        } catch (AccountOperationNotPermitted e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            HttpFinResponse<String> finResponse = new HttpFinResponse<>(HttpFinResponse.Status.ERROR, e.getMessage());
            response.getWriter().println(objectMapper.writeValueAsString(finResponse));
        }
    }

}
