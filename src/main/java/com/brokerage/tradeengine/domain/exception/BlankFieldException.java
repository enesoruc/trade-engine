package com.brokerage.tradeengine.domain.exception;

public class BlankFieldException extends DomainException {

    public BlankFieldException(String fieldName) {
        super(
                "BLANK_FIELD",
                "error.field.blank",
                DomainErrorType.VALIDATION,
                fieldName + " cannot be blank",
                fieldName
        );
    }
}
