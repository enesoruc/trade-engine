package com.brokerage.tradeengine.application.usecase;

import com.brokerage.tradeengine.application.dto.mapper.OrderItemResponseMapper;
import com.brokerage.tradeengine.application.dto.response.OrderItemResponse;
import com.brokerage.tradeengine.domain.model.Order;
import com.brokerage.tradeengine.domain.model.OrderSide;
import com.brokerage.tradeengine.domain.model.Status;
import com.brokerage.tradeengine.domain.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListOrdersUseCaseTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemResponseMapper orderMapper;

    @InjectMocks
    private ListOrdersUseCase listOrdersUseCase;

    @Test
    void execute_shouldFilterWithGivenCriteriaAndMapResults() {
        String customerId = "cust-1";
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now();
        Status status = Status.PENDING;
        OrderSide side = OrderSide.BUY;
        String assetName = "AAPL";

        Order order = new Order(
                11L,
                customerId,
                assetName,
                side,
                new BigDecimal("2.00"),
                new BigDecimal("100.00"),
                status,
                LocalDateTime.now()
        );
        OrderItemResponse response = new OrderItemResponse(
                order.getId(),
                order.getCustomerId(),
                order.getAssetName(),
                order.getOrderSide().name(),
                order.getSize(),
                order.getPrice(),
                order.getStatus().name(),
                order.getCreateDate()
        );

        when(orderRepository.findByFilters(customerId, startDate, endDate, status, side, assetName))
                .thenReturn(List.of(order));
        when(orderMapper.map(order)).thenReturn(response);

        List<OrderItemResponse> result = listOrdersUseCase.execute(customerId, startDate, endDate, status, side, assetName);

        assertEquals(1, result.size());
        assertEquals(11L, result.getFirst().orderId());
        assertEquals("AAPL", result.getFirst().assetName());
        verify(orderRepository).findByFilters(customerId, startDate, endDate, status, side, assetName);
        verify(orderMapper).map(order);
    }
}
