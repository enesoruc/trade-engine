package com.brokerage.tradeengine.application.port.in;

import com.brokerage.tradeengine.application.dto.response.OrderItemResponse;
import com.brokerage.tradeengine.domain.model.OrderSide;
import com.brokerage.tradeengine.domain.model.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface ListOrdersInputPort {

    List<OrderItemResponse> execute(
            String customerId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Status status,
            OrderSide orderSide,
            String assetName
    );
}
