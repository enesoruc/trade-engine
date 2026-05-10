package com.brokerage.tradeengine.domain.exception;

public class InsufficientUsableSizeException extends DomainException {

    public InsufficientUsableSizeException() {
        super(
                "INSUFFICIENT_USABLE_SIZE",
                "error.asset.insufficient-usable-size",
                DomainErrorType.VALIDATION,
                "insufficient usableSize to block"
        );
    }
}
