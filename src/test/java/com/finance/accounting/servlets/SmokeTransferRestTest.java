package com.finance.accounting.servlets;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.finance.accounting.app.FinServer;
import com.finance.accounting.models.Transfer;
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

public class SmokeTransferRestTest {

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

        try {
            for (int i = 0; i < (ORDERS_COUNT -1); ++i) {
                HttpClient httpClient = HttpClientBuilder.create().build();
                HttpPost request = new HttpPost("http://localhost:" + LISTEN_PORT + "/transfers");
                StringEntity params = new StringEntity("{\n" +
                        "\"senderIban\":\"IBAN00012345000000000000985" + i + "\",\n" +
                        "\"recipientIban\":\"IBAN000123450000000000000000\",\n" +
                        "\"amount\":\"" + i + "\",\n" +
                        "\"currency\":\"EUR\",\n" +
                        "\"notes\":\"Own funds\",\n" +
                        "\"createdBy\":\"1\"\n" +
                        "}");
                request.addHeader("content-type", "application/json");
                request.setEntity(params);
                HttpResponse response = httpClient.execute(request);
                assertEquals(200, response.getStatusLine().getStatusCode());
            }

            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost("http://localhost:" + LISTEN_PORT + "/transfers");
            StringEntity params = new StringEntity("{\n" +
                    "\"senderIban\":\"IBAN000123450000000000000000\",\n" +
                    "\"recipientIban\":\"IBAN000123450000000000000000\",\n" +
                    "\"amount\":\"1000\",\n" +
                    "\"currency\":\"EUR\",\n" +
                    "\"notes\":\"Own funds\",\n" +
                    "\"createdBy\":\"1\"\n" +
                    "}");
            request.addHeader("content-type", "application/json");
            request.setEntity(params);
            HttpResponse response = httpClient.execute(request);
            assertEquals(400, response.getStatusLine().getStatusCode());

        } catch (Exception e) {
            e.printStackTrace();
        }

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet("http://localhost:" + LISTEN_PORT + "/transfers");
        request.addHeader("content-type", "application/json");
        HttpResponse response = httpClient.execute(request);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        HttpFinResponse<List<Transfer>> finResponse = objectMapper.readValue(response.getEntity().getContent(), new TypeReference<HttpFinResponse<List<Transfer>>>() {
        });

        List<Transfer> transfers = finResponse.getBody();
        assertEquals(HttpStatus.OK_200, response.getStatusLine().getStatusCode());
        assertEquals(ORDERS_COUNT/* + 20*/, transfers.size());
    }

}