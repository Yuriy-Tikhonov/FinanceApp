package com.finance.accounting.servlets;

import com.google.inject.servlet.ServletModule;

public class FinServletModule extends ServletModule {
    @Override
    protected void configureServlets() {
        bind(AccountsServlet.class);
        bind(TransfersServlet.class);
//        bind(IndexServlet.class); //TODO: index to swagger

        serve("/accounts").with(AccountsServlet.class);
        serve("/transfers").with(TransfersServlet.class);
//        serve("/*").with(IndexServlet.class);
    }

}
