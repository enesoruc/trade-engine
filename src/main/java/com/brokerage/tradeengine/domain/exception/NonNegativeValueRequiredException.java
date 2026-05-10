package com.brokerage.tradeengine.domain.exception;

public class NonNegativeValueRequiredException extends DomainException {

    public NonNegativeValueRequiredException(String fieldName) {
        super(
                "VALUE_MUST_BE_NON_NEGATIVE",
                "error.field.non-negative",
                DomainErrorType.VALIDATION,
                fieldName + " cannot be negative",
                fieldName
        );
    }
}
