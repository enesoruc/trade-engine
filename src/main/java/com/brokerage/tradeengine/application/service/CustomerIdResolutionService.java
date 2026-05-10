package com.brokerage.tradeengine.application.service;

import com.brokerage.tradeengine.application.exception.CustomerResourceAccessDeniedException;
import com.brokerage.tradeengine.application.exception.MissingCustomerIdException;

public class CustomerIdResolutionService {

    public String resolve(String authenticatedCustomerId, boolean isAdmin, String requestedCustomerId) {
        if (isAdmin) {
            if (requestedCustomerId == null || requestedCustomerId.isBlank()) {
                throw new MissingCustomerIdException();
            }
            return requestedCustomerId;
        }

        if (requestedCustomerId != null
                && !requestedCustomerId.isBlank()
                && !authenticatedCustomerId.equals(requestedCustomerId)) {
            throw new CustomerResourceAccessDeniedException();
        }

        return authenticatedCustomerId;
    }
}
