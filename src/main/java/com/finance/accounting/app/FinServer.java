package com.finance.accounting.app;

import com.finance.accounting.guice.*;
import com.finance.accounting.servlets.FinServletModule;
import com.finance.accounting.stores.api.AccountsGrantsStore;
import com.finance.accounting.stores.api.AccountsStore;
import com.finance.accounting.stores.api.OrdersStore;
import com.finance.accounting.stores.impl.AccountsGrantsInMemoryStore;
import com.finance.accounting.stores.impl.AccountsInMemoryStore;
import com.finance.accounting.stores.impl.OrdersInMemoryStore;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;

import javax.servlet.DispatcherType;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Properties;

public final class FinServer {

    private static final FinServer ourInstance = new FinServer();

    private Server server;

    private static AccountsStore accountsStore;
    private static AccountsGrantsStore accountsGrantsStore;
    private static OrdersStore transfersStore;

    public static FinServer getInstance() {
        return ourInstance;
    }

    private FinServer() {
    }

    public void InjectModules() {
        Injector injector = Guice.createInjector(
                new AccountsStoreModule(),
                new AccountGrantsStoreModule(),
                new AccountsServiceModule(),
                new OrdersStoreModule(),
                new TransfersServiceModule(),
                new FinServletModule(),
                new OrderProcessingServiceModule()
        );

        accountsStore = injector.getInstance(AccountsInMemoryStore.class);
        accountsGrantsStore = injector.getInstance(AccountsGrantsInMemoryStore.class);
        transfersStore = injector.getInstance(OrdersInMemoryStore.class);
    }

    public void startServer() throws Exception {
        String rootPath = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("")).getPath();
        String appConfigPath = rootPath + "application.properties";

        Properties appProps = new Properties();
        appProps.load(new FileInputStream(appConfigPath));
        int LISTEN_PORT = Integer.parseInt(appProps.getProperty("server.listenPort"));

        server = new Server(LISTEN_PORT);
        ServletContextHandler servletContextHandler = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS);
        servletContextHandler.addFilter(GuiceFilter.class, "/*", EnumSet.allOf(DispatcherType.class));
        servletContextHandler.addServlet(DefaultServlet.class, "/");
        server.start();
    }

    public Server getServer() {
        return server;
    }

    public static void clear() throws IOException {
        accountsStore.clear();
        accountsGrantsStore.clear();
        transfersStore.clear();
    }
}
