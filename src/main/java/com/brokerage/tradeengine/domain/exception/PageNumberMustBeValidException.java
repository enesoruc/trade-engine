package com.brokerage.tradeengine.domain.exception;

public class PageNumberMustBeValidException extends DomainException{

    public PageNumberMustBeValidException() {
        super(
                "PAGE_NUMBER_MUST_BE_VALID",
                "error.page.number.must-be-valid",
                DomainErrorType.VALIDATION,
                "Page number must be at least 1"
        );
    }
}
