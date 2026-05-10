package com.brokerage.tradeengine.domain.exception;

public class PositiveValueRequiredException extends DomainException {

    public PositiveValueRequiredException(String fieldName) {
        super(
                "VALUE_MUST_BE_POSITIVE",
                "error.field.positive",
                DomainErrorType.VALIDATION,
                fieldName + " must be positive",
                fieldName
        );
    }
}
