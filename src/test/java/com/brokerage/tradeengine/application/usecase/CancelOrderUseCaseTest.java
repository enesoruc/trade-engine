package com.brokerage.tradeengine.application.usecase;

import com.brokerage.tradeengine.application.dto.mapper.OrderItemResponseMapper;
import com.brokerage.tradeengine.application.dto.request.CancelOrderRequest;
import com.brokerage.tradeengine.application.dto.response.OrderItemResponse;
import com.brokerage.tradeengine.application.exception.OrderOwnershipViolationException;
import com.brokerage.tradeengine.domain.model.Asset;
import com.brokerage.tradeengine.domain.model.Order;
import com.brokerage.tradeengine.domain.model.OrderSide;
import com.brokerage.tradeengine.domain.model.Status;
import com.brokerage.tradeengine.domain.repository.AssetRepository;
import com.brokerage.tradeengine.domain.repository.OrderRepository;
import com.brokerage.tradeengine.domain.service.OrderBalanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CancelOrderUseCaseTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private AssetRepository assetRepository;

    private final OrderItemResponseMapper orderMapper = new OrderItemResponseMapper();
    private final OrderBalanceService orderBalanceService = new OrderBalanceService();

    private CancelOrderUseCase cancelOrderUseCase;

    @BeforeEach
    void setUp() {
        cancelOrderUseCase = new CancelOrderUseCase(
                orderRepository,
                assetRepository,
                orderMapper,
                orderBalanceService
        );
    }

    @Test
    void execute_shouldCancelBuyOrderAndReleaseTryReservation() {
        Order order = new Order(
                10L, "cust-1", "AAPL", OrderSide.BUY,
                new BigDecimal("2.0000"), new BigDecimal("100.00"),
                Status.PENDING, LocalDateTime.now()
        );
        Asset tryAsset = new Asset("cust-1", "TRY", new BigDecimal("1000.0000"), new BigDecimal("800.0000"));

        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));
        when(assetRepository.findByCustomerIdAndAssetName("cust-1", "TRY")).thenReturn(Optional.of(tryAsset));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderItemResponse response = cancelOrderUseCase.execute(new CancelOrderRequest(10L, "cust-1"));

        assertEquals(Status.CANCELED.name(), response.status());
        assertEquals(new BigDecimal("1000.0000"), tryAsset.getUsableSize());
        verify(assetRepository).save(tryAsset);
    }

    @Test
    void execute_shouldCancelSellOrderAndReleaseStockReservation() {
        Order order = new Order(
                11L, "cust-1", "AAPL", OrderSide.SELL,
                new BigDecimal("3.5000"), new BigDecimal("90.00"),
                Status.PENDING, LocalDateTime.now()
        );
        Asset stockAsset = new Asset("cust-1", "AAPL", new BigDecimal("10.0000"), new BigDecimal("6.5000"));

        when(orderRepository.findById(11L)).thenReturn(Optional.of(order));
        when(assetRepository.findByCustomerIdAndAssetName("cust-1", "AAPL")).thenReturn(Optional.of(stockAsset));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderItemResponse response = cancelOrderUseCase.execute(new CancelOrderRequest(11L, "cust-1"));

        assertEquals(Status.CANCELED.name(), response.status());
        assertEquals(new BigDecimal("10.0000"), stockAsset.getUsableSize());
        verify(assetRepository).save(stockAsset);
    }

    @Test
    void execute_shouldThrow_whenCustomerDoesNotOwnOrder() {
        Order order = new Order(
                12L, "cust-1", "AAPL", OrderSide.SELL,
                new BigDecimal("1.0000"), new BigDecimal("90.00"),
                Status.PENDING, LocalDateTime.now()
        );

        when(orderRepository.findById(12L)).thenReturn(Optional.of(order));

        assertThrows(
                OrderOwnershipViolationException.class,
                () -> cancelOrderUseCase.execute(new CancelOrderRequest(12L, "cust-2"))
        );
    }
}
