package com.brokerage.tradeengine.domain.exception;

import lombok.Getter;

public abstract class DomainException extends RuntimeException {

    @Getter
    private final String code;
    @Getter
    private final String messageKey;
    @Getter
    private final DomainErrorType errorType;
    private final Object[] messageArgs;

    protected DomainException(
            String code,
            String messageKey,
            DomainErrorType errorType,
            String defaultMessage,
            Object... messageArgs
    ) {
        super(defaultMessage);
        this.code = code;
        this.messageKey = messageKey;
        this.errorType = errorType;
        this.messageArgs = messageArgs == null ? new Object[0] : messageArgs.clone();
    }

    public Object[] getMessageArgs() {
        return messageArgs.clone();
    }
}
