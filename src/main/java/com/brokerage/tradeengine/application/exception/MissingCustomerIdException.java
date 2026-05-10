package com.brokerage.tradeengine.application.exception;

import org.springframework.http.HttpStatus;

public class MissingCustomerIdException extends ApplicationException {

    public MissingCustomerIdException() {
        super(
                "MISSING_CUSTOMER_ID",
                "error.customer.missing-id",
                HttpStatus.BAD_REQUEST,
                "Admin requests must include customerId."
        );
    }
}
