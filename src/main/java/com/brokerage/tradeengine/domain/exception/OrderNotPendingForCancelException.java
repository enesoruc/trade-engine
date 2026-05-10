package com.brokerage.tradeengine.domain.exception;

public class OrderNotPendingForCancelException extends DomainException {

    public OrderNotPendingForCancelException() {
        super(
                "ORDER_NOT_PENDING_FOR_CANCEL",
                "error.order.not-pending-cancel",
                DomainErrorType.BUSINESS_RULE_CONFLICT,
                "Only PENDING orders can be canceled"
        );
    }
}
