package com.brokerage.tradeengine.application.usecase;

import com.brokerage.tradeengine.application.dto.mapper.OrderItemResponseMapper;
import com.brokerage.tradeengine.application.dto.response.OrderItemResponse;
import com.brokerage.tradeengine.domain.common.PagedResult;
import com.brokerage.tradeengine.application.port.in.ListOrdersInputPort;
import com.brokerage.tradeengine.domain.model.Order;
import com.brokerage.tradeengine.domain.model.OrderSide;
import com.brokerage.tradeengine.domain.model.Status;
import com.brokerage.tradeengine.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class ListOrdersUseCase implements ListOrdersInputPort {

    private final OrderRepository orderRepository;
    private final OrderItemResponseMapper orderMapper;

    @Transactional(readOnly = true)
    @Override
    public PagedResult<OrderItemResponse> execute(
            String customerId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Status status,
            OrderSide orderSide,
            String assetName,
            int pageNumber,
            int pageSize
    ) {
        PagedResult<Order> orders = orderRepository
                .findByFilters(customerId, startDate, endDate, status, orderSide, assetName, pageNumber, pageSize);
        var mappedOrders = orders.content().stream().map(orderMapper::map).toList();
        return PagedResult.of(mappedOrders, orders.totalPages(), orders.totalElements());
    }
}
