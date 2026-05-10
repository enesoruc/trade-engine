package com.brokerage.tradeengine.application.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public abstract class ApplicationException extends RuntimeException {

    @Getter
    private final String code;
    @Getter
    private final String messageKey;
    @Getter
    private final HttpStatus httpStatus;
    private final Object[] messageArgs;

    protected ApplicationException(String code, String messageKey, HttpStatus httpStatus, String defaultMessage, Object... messageArgs) {
        this(code, messageKey, httpStatus, defaultMessage, null, messageArgs);
    }

    protected ApplicationException(
            String code,
            String messageKey,
            HttpStatus httpStatus,
            String defaultMessage,
            Throwable cause,
            Object... messageArgs
    ) {
        super(defaultMessage, cause);
        this.code = code;
        this.messageKey = messageKey;
        this.httpStatus = httpStatus;
        this.messageArgs = messageArgs == null ? new Object[0] : messageArgs.clone();
    }

    public Object[] getMessageArgs() {
        return messageArgs.clone();
    }
}
