package com.brokerage.tradeengine.application.exception;

import org.springframework.http.HttpStatus;

public class CustomerResourceAccessDeniedException extends ApplicationException {

    public CustomerResourceAccessDeniedException() {
        super(
                "CUSTOMER_RESOURCE_ACCESS_DENIED",
                "error.customer.access-denied",
                HttpStatus.FORBIDDEN,
                "Customer can only access own resources."
        );
    }
}
