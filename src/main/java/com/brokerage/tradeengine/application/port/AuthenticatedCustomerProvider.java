package com.brokerage.tradeengine.application.port;

public interface AuthenticatedCustomerProvider {

    String getAuthenticatedCustomerId();

    boolean isAdmin();
}
