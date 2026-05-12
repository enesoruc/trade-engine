package com.brokerage.tradeengine.application.port.in;

import com.brokerage.tradeengine.application.dto.response.OrderItemResponse;
import com.brokerage.tradeengine.domain.common.PagedResult;
import com.brokerage.tradeengine.domain.model.OrderSide;
import com.brokerage.tradeengine.domain.model.Status;

import java.time.LocalDateTime;

public interface ListOrdersInputPort {

    PagedResult<OrderItemResponse> execute(
            String customerId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Status status,
            OrderSide orderSide,
            String assetName,
            int pageNumber,
            int pageSize
    );
}
