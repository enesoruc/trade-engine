package com.brokerage.tradeengine.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateOrderResponse(
        String customerId,
        String assetName,
        String orderSide,
        BigDecimal size,
        BigDecimal price,
        BigDecimal tryUsableSize,
        String status,
        LocalDateTime createDate
) {
}
