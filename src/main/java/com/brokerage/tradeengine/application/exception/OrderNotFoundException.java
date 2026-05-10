package com.brokerage.tradeengine.application.exception;

import org.springframework.http.HttpStatus;

public class OrderNotFoundException extends ApplicationException {

    public OrderNotFoundException(Long orderId) {
        super(
                "ORDER_NOT_FOUND",
                "error.order.not-found",
                HttpStatus.NOT_FOUND,
                "Order not found: " + orderId,
                orderId
        );
    }
}
