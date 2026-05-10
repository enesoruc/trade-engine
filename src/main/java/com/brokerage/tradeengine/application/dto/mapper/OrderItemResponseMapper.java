package com.brokerage.tradeengine.application.dto.mapper;

import com.brokerage.tradeengine.application.dto.response.OrderItemResponse;
import com.brokerage.tradeengine.domain.model.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderItemResponseMapper {

    public OrderItemResponse map(Order order) {
        return new OrderItemResponse(
                order.getId(),
                order.getCustomerId(),
                order.getAssetName(),
                order.getOrderSide().name(),
                order.getSize(),
                order.getPrice(),
                order.getStatus().name(),
                order.getCreateDate()
        );
    }
}