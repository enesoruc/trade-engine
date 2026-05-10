package com.brokerage.tradeengine.application.dto.response;

import java.time.Instant;

public record ErrorResponse(
        String code,
        String message,
        Instant timestamp
) {
}
