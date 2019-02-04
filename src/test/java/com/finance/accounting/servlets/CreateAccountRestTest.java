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
import org.eclipse.jetty.http.HttpStatus;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileInputStream;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class CreateAccountRestTest {

    private final static int ORDERS_COUNT = 50;

    @BeforeClass
    public static void startJetty() throws Exception {
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
    public void testTransfers() throws Exception {
        String rootPath = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("")).getPath();
        String appConfigPath = rootPath + "application.properties";

        Properties appProps = new Properties();
        appProps.load(new FileInputStream(appConfigPath));
        int LISTEN_PORT = Integer.parseInt(appProps.getProperty("server.listenPort"));

        int clientIdCheckCount = 0;

        for (int i = 0; i < ORDERS_COUNT; ++i) {
                try {
                    int clientId = (i/5)+1;
                    if(clientId == 1){
                        clientIdCheckCount++;
                    }

                    HttpClient httpClient = HttpClientBuilder.create().build();
                    HttpPost request = new HttpPost("http://localhost:" + LISTEN_PORT + "/accounts");
                    StringEntity params = new StringEntity("{\n" +
                            "\"iban\":\"IBAN0001234500000000000000" + i + "\",\n" +
                            "\"bic\":\"BIC00" + i + "\",\n" +
                            "\"amount\":\"" + i + "\",\n" +
                            "\"currency\":\"EUR\",\n" +
                            "\"clientId\":\"" + clientId + "\"\n" +
                            "}");
                    request.addHeader("content-type", "application/json");
                    request.setEntity(params);
                    HttpResponse response = httpClient.execute(request);
                    assertEquals(200, response.getStatusLine().getStatusCode());
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet("http://localhost:" + LISTEN_PORT + "/accounts?clientId=1");
        request.addHeader("content-type", "application/json");
        HttpResponse response = httpClient.execute(request);

        ObjectMapper objectMapper = new ObjectMapper();
        HttpFinResponse<List<AccountDetails>> finResponse = objectMapper.readValue(response.getEntity().getContent(), new TypeReference<HttpFinResponse<List<AccountDetails>>>() {});

        List<AccountDetails> accounts = finResponse.getBody();
        assertEquals(HttpStatus.OK_200, response.getStatusLine().getStatusCode());
        assertEquals(clientIdCheckCount+3, accounts.size());
    }

}