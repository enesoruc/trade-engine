package com.brokerage.tradeengine.domain.exception;

public class UsableSizeExceedsSizeException extends DomainException {

    public UsableSizeExceedsSizeException() {
        super(
                "USABLE_SIZE_EXCEEDS_SIZE",
                "error.asset.usable-size-exceeds-size",
                DomainErrorType.VALIDATION,
                "usableSize cannot be greater than size"
        );
    }
}
