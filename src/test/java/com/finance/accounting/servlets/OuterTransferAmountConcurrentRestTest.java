package com.finance.accounting.servlets;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finance.accounting.app.FinServer;
import com.finance.accounting.models.AccountDetails;
import com.finance.accounting.responses.HttpFinResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class OuterTransferAmountConcurrentRestTest {

    private final static int THREAD_COUNT = 10;
    private static int LISTEN_PORT;

    @BeforeClass
    public static void startJetty() throws Exception {
        String rootPath = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("")).getPath();
        String appConfigPath = rootPath + "application.properties";

        Properties appProps = new Properties();
        appProps.load(new FileInputStream(appConfigPath));
        LISTEN_PORT = Integer.parseInt(appProps.getProperty("server.listenPort"));

        FinServer finServer = FinServer.getInstance();
        finServer.InjectModules();
        finServer.startServer();
    }

    @AfterClass
    public static void stopJetty() throws Exception {
        FinServer.getInstance().getServer().stop();
        try {
            FinServer.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void transferMoney() {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        for (int i = 0; i < THREAD_COUNT; ++i) {
            executor.execute(() -> {
                try {
                    HttpClient httpClient = HttpClientBuilder.create().build();
                    HttpPost request = new HttpPost("http://localhost:" + LISTEN_PORT + "/transfers");
                    StringEntity params = new StringEntity("{\n" +
                            "\"senderIban\":\"IBAN000123450000000000000001\",\n" +
                            "\"recipientIban\":\"HU42117730161OuterAccount\",\n" +
                            "\"amount\":\"1000.0\",\n" +
                            "\"currency\":\"EUR\",\n" +
                            "\"notes\":\"Own funds\",\n" +
                            "\"createdBy\":\"1\"\n" +
                            "}");
                    request.addHeader("content-type", "application/json");
                    request.setEntity(params);
                    HttpResponse response = httpClient.execute(request);
                    assertEquals(200, response.getStatusLine().getStatusCode());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        executor.shutdown();
        try {
            executor.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @After
    public void testRestAmount() throws Exception {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet("http://localhost:" + LISTEN_PORT + "/accounts?clientId=1");
        HttpResponse response = httpClient.execute(request);
        assertEquals(200, response.getStatusLine().getStatusCode());
        ObjectMapper objectMapper = new ObjectMapper();
        HttpFinResponse<List<AccountDetails>> finResponse = objectMapper.readValue(response.getEntity().getContent(), new TypeReference<HttpFinResponse<List<AccountDetails>>>() {});
        List<AccountDetails> accounts = finResponse.getBody();
        AccountDetails needAccount = null;
        for (AccountDetails account : accounts) {
            if (account.getAccount().getIban().equals("IBAN000123450000000000000001")) {
                needAccount = account;
                break;
            }
        }
        assertNotNull(needAccount);
        assertEquals(0, needAccount.getAmount().compareTo(BigDecimal.ZERO));
    }

}
