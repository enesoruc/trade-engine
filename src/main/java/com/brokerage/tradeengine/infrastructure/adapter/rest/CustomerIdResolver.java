package com.brokerage.tradeengine.infrastructure.adapter.rest;

import com.brokerage.tradeengine.application.port.AuthenticatedCustomerProvider;
import com.brokerage.tradeengine.application.service.CustomerIdResolutionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerIdResolver {

    private final CustomerIdResolutionService customerIdResolutionService;
    private final AuthenticatedCustomerProvider authenticatedCustomerProvider;

    public String resolve(String requestedCustomerId) {
        return customerIdResolutionService.resolve(
                authenticatedCustomerProvider.getAuthenticatedCustomerId(),
                authenticatedCustomerProvider.isAdmin(),
                requestedCustomerId
        );
    }
}
