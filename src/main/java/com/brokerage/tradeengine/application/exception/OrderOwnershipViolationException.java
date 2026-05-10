package com.brokerage.tradeengine.application.exception;

import org.springframework.http.HttpStatus;

public class OrderOwnershipViolationException extends ApplicationException {

    public OrderOwnershipViolationException(String customerId) {
        super(
                "ORDER_OWNERSHIP_VIOLATION",
                "error.order.ownership-violation",
                HttpStatus.FORBIDDEN,
                "Order does not belong to customer: " + customerId,
                customerId
        );
    }
}
