package com.brokerage.tradeengine.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderItemResponse(
        Long orderId,
        String customerId,
        String assetName,
        String orderSide,
        BigDecimal size,
        BigDecimal price,
        String status,
        LocalDateTime createDate
) {
}
