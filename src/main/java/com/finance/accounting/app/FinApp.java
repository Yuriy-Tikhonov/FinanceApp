package com.finance.accounting.app;

class FinApp {
    public static void main(String[] args) throws Exception {
        FinServer finServer = FinServer.getInstance();
        finServer.InjectModules();
        finServer.startServer();
        finServer.getServer().join();
    }

}
