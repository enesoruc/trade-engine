package com.brokerage.tradeengine.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CancelOrderRequest(
        @NotNull @Positive Long orderId,
        @NotBlank String customerId
) {
}
