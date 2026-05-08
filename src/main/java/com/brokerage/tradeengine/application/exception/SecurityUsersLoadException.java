package com.brokerage.tradeengine.application.exception;

import org.springframework.http.HttpStatus;

public class SecurityUsersLoadException extends ApplicationException {

    public SecurityUsersLoadException(Throwable cause) {
        super(
                "SECURITY_USERS_LOAD_ERROR",
                "error.security.users-load",
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Could not load security users from initial-data.json",
                cause
        );
    }
}
