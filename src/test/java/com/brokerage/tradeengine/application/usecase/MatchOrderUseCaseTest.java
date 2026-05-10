package com.brokerage.tradeengine.application.usecase;

import com.brokerage.tradeengine.domain.exception.AssetNotFoundException;
import com.brokerage.tradeengine.domain.model.Asset;
import com.brokerage.tradeengine.domain.model.Order;
import com.brokerage.tradeengine.domain.model.OrderSide;
import com.brokerage.tradeengine.domain.model.Status;
import com.brokerage.tradeengine.domain.repository.AssetRepository;
import com.brokerage.tradeengine.domain.repository.OrderRepository;
import com.brokerage.tradeengine.domain.service.OrderBalanceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MatchOrderUseCaseTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private TransactionTemplate transactionTemplate;

    @Spy
    private OrderBalanceService orderBalanceService;

    @InjectMocks
    private MatchOrderUseCase matchOrderUseCase;

    @Test
    void execute_shouldMatchPendingOrdersAndPersistChanges() {
        Order buyOrder = new Order(
                1L,
                "cust-1",
                "AAPL",
                OrderSide.BUY,
                new BigDecimal("2.00"),
                new BigDecimal("100.00"),
                Status.PENDING,
                LocalDateTime.now()
        );
        List<Order> firstBatch = List.of(buyOrder);

        Asset tryAsset = new Asset("cust-1", "TRY", new BigDecimal("1000.00"), new BigDecimal("800.00"));
        Asset stockAsset = new Asset("cust-1", "AAPL", new BigDecimal("10.00"), new BigDecimal("10.00"));

        when(orderRepository.findPendingOrders(500)).thenReturn(firstBatch, List.of());
        when(assetRepository.findByCustomerIdAndAssetName("cust-1", "TRY")).thenReturn(Optional.of(tryAsset));
        when(assetRepository.findByCustomerIdAndAssetName("cust-1", "AAPL")).thenReturn(Optional.of(stockAsset));
        doAnswer(invocation -> {
            Consumer<Object> callback = invocation.getArgument(0);
            callback.accept(null);
            return null;
        }).when(transactionTemplate).executeWithoutResult(any());

        matchOrderUseCase.execute();

        assertEquals(Status.MATCHED, buyOrder.getStatus());
        assertEquals(new BigDecimal("800.00"), tryAsset.getSize());
        assertEquals(new BigDecimal("210.00"), stockAsset.getSize());
        verify(assetRepository).saveAll(any());
        verify(orderRepository).saveAll(firstBatch);
    }

    @Test
    void execute_shouldThrowWhenTryAssetDoesNotExist() {
        Order buyOrder = new Order(
                1L,
                "cust-1",
                "AAPL",
                OrderSide.BUY,
                new BigDecimal("1.00"),
                new BigDecimal("50.00"),
                Status.PENDING,
                LocalDateTime.now()
        );
        when(orderRepository.findPendingOrders(500)).thenReturn(List.of(buyOrder));
        when(assetRepository.findByCustomerIdAndAssetName("cust-1", "TRY")).thenReturn(Optional.empty());
        doAnswer(invocation -> {
            Consumer<Object> callback = invocation.getArgument(0);
            callback.accept(null);
            return null;
        }).when(transactionTemplate).executeWithoutResult(any());

        assertThrows(AssetNotFoundException.class, () -> matchOrderUseCase.execute());
        verify(orderRepository, never()).saveAll(any());
    }
}
