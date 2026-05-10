package com.brokerage.tradeengine.application.usecase;

import com.brokerage.tradeengine.application.dto.mapper.OrderItemResponseMapper;
import com.brokerage.tradeengine.application.dto.response.OrderItemResponse;
import com.brokerage.tradeengine.application.port.in.ListOrdersInputPort;
import com.brokerage.tradeengine.domain.model.OrderSide;
import com.brokerage.tradeengine.domain.model.Status;
import com.brokerage.tradeengine.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ListOrdersUseCase implements ListOrdersInputPort {

    private final OrderRepository orderRepository;
    private final OrderItemResponseMapper orderMapper;

    @Transactional(readOnly = true)
    @Override
    public List<OrderItemResponse> execute(
            String customerId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Status status,
            OrderSide orderSide,
            String assetName
    ) {
        return orderRepository.findByFilters(customerId, startDate, endDate, status, orderSide, assetName)
                .stream()
                .map(orderMapper::map)
                .toList();
    }
}
