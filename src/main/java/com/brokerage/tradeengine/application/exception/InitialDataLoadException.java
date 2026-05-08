package com.brokerage.tradeengine.application.exception;

import org.springframework.http.HttpStatus;

public class InitialDataLoadException extends ApplicationException {

    public InitialDataLoadException(String path, Throwable cause) {
        super(
                "INITIAL_DATA_LOAD_ERROR",
                "error.initial-data.load",
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Cannot load initial data from " + path,
                cause,
                path
        );
    }
}
