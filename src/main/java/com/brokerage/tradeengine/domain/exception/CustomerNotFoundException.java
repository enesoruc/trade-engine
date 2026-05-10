package com.brokerage.tradeengine.domain.exception;

public class CustomerNotFoundException extends DomainException {

    public CustomerNotFoundException(String customerId) {
        super(
                "CUSTOMER_NOT_FOUND",
                "error.customer.not-found",
                DomainErrorType.NOT_FOUND,
                "Customer not found: " + customerId,
                customerId
        );
    }
}
