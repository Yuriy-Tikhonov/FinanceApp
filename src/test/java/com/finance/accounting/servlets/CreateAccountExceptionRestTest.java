package com.finance.accounting.servlets;

import com.finance.accounting.app.FinServer;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.*;

import java.io.FileInputStream;
import java.util.Objects;
import java.util.Properties;

import static org.junit.Assert.*;

public class CreateAccountExceptionRestTest {

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
    public void testAccounts() throws Exception {
        String rootPath = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("")).getPath();
        String appConfigPath = rootPath + "application.properties";

        Properties appProps = new Properties();
        appProps.load(new FileInputStream(appConfigPath));
        int LISTEN_PORT = Integer.parseInt(appProps.getProperty("server.listenPort"));

        try {
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost("http://localhost:" + LISTEN_PORT + "/accounts");
            StringEntity params = new StringEntity("{\n" +
                    "\"iban\":\"IBAN000123450000000000000000\",\n" +
                    "\"bic\":\"BIC00X\",\n" +
                    "\"amount\":\"1234\",\n" +
                    "\"currency\":\"EUR\",\n" +
                    "\"clientId\":\"1\"\n" +
                    "}");
            request.addHeader("content-type", "application/json");
            request.setEntity(params);
            HttpResponse response = httpClient.execute(request);
            assertEquals(400, response.getStatusLine().getStatusCode());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}