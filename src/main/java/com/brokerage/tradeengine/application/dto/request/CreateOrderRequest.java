package com.brokerage.tradeengine.application.dto.request;

import com.brokerage.tradeengine.domain.model.OrderSide;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateOrderRequest(
        String customerId,
        @NotBlank String assetName,
        @NotNull OrderSide side,
        @NotNull @Positive BigDecimal size,
        @NotNull @Positive BigDecimal price
) {
}
