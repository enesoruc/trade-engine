package com.brokerage.tradeengine.domain.exception;

public class PageSizeMustBeValidException extends DomainException{

    public PageSizeMustBeValidException() {
        super(
                "PAGE_SIZE_MUST_BE_VALID",
                "error.page.size.must-be-valid",
                DomainErrorType.VALIDATION,
                "Page size must be between 1 and 100"
        );
    }
}
