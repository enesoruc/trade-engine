package com.brokerage.tradeengine.domain.exception;

public class OrderNotPendingForMatchException extends DomainException {

    public OrderNotPendingForMatchException() {
        super(
                "ORDER_NOT_PENDING_FOR_MATCH",
                "error.order.not-pending-match",
                DomainErrorType.BUSINESS_RULE_CONFLICT,
                "Only PENDING orders can be matched"
        );
    }
}
