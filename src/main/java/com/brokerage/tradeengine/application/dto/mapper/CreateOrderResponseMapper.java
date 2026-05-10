package com.brokerage.tradeengine.application.dto.mapper;

import com.brokerage.tradeengine.application.dto.response.CreateOrderResponse;
import com.brokerage.tradeengine.domain.model.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CreateOrderResponseMapper {

    public CreateOrderResponse map(Order order, BigDecimal tryUsableSize) {
        return new CreateOrderResponse(
                order.getCustomerId(),
                order.getAssetName(),
                order.getOrderSide().name(),
                order.getSize(),
                order.getPrice(),
                tryUsableSize,
                order.getStatus().name(),
                order.getCreateDate()
        );
    }
}
