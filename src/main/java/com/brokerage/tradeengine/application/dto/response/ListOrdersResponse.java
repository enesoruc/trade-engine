package com.brokerage.tradeengine.application.dto.response;

import java.util.List;

public record ListOrdersResponse(
        List<OrderItemResponse> orders
) {
}
