package com.brokerage.tradeengine.application.usecase;

import com.brokerage.tradeengine.application.dto.mapper.CreateOrderResponseMapper;
import com.brokerage.tradeengine.application.dto.request.CreateOrderRequest;
import com.brokerage.tradeengine.application.dto.response.CreateOrderResponse;
import com.brokerage.tradeengine.domain.exception.AssetNotFoundException;
import com.brokerage.tradeengine.domain.exception.InsufficientUsableSizeException;
import com.brokerage.tradeengine.domain.exception.TryAssetNotAllowedException;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateOrderUseCaseTest {

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private OrderRepository orderRepository;

    private final CreateOrderResponseMapper mapper = new CreateOrderResponseMapper();
    private final OrderBalanceService orderBalanceService = new OrderBalanceService();

    private CreateOrderUseCase createOrderUseCase;

    @BeforeEach
    void setUp() {
        createOrderUseCase = new CreateOrderUseCase(assetRepository, orderRepository, mapper, orderBalanceService);
    }

    @Test
    void execute_shouldReserveTryBalanceAndCreatePendingBuyOrder() {
        CreateOrderRequest request = new CreateOrderRequest(
                "cust-1", "AAPL", OrderSide.BUY, new BigDecimal("2.0000"), new BigDecimal("100.00")
        );
        Asset tryAsset = new Asset("cust-1", "TRY", new BigDecimal("1000.0000"), new BigDecimal("1000.0000"));

        when(assetRepository.findByCustomerIdAndAssetName("cust-1", "TRY")).thenReturn(Optional.of(tryAsset));

        CreateOrderResponse response = createOrderUseCase.execute(request);

        assertEquals(Status.PENDING.name(), response.status());
        assertEquals(new BigDecimal("800.00"), tryAsset.getUsableSize());

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        assertEquals(OrderSide.BUY, orderCaptor.getValue().getOrderSide());
        assertEquals(new BigDecimal("2.00"), orderCaptor.getValue().getSize());
    }

    @Test
    void execute_shouldReserveStockBalanceAndCreatePendingSellOrder() {
        CreateOrderRequest request = new CreateOrderRequest(
                "cust-1", "AAPL", OrderSide.SELL, new BigDecimal("3.50"), new BigDecimal("1.00")
        );
        Asset stockAsset = new Asset("cust-1", "AAPL", new BigDecimal("10.0000"), new BigDecimal("10.0000"));
        Asset tryAsset = new Asset("cust-1", "TRY", new BigDecimal("0.0000"), new BigDecimal("0.0000"));

        when(assetRepository.findByCustomerIdAndAssetName("cust-1", "AAPL")).thenReturn(Optional.of(stockAsset));
        when(assetRepository.findByCustomerIdAndAssetName("cust-1", "TRY")).thenReturn(Optional.of(tryAsset));

        CreateOrderResponse response = createOrderUseCase.execute(request);

        assertEquals(Status.PENDING.name(), response.status());
        assertEquals(new BigDecimal("6.50"), stockAsset.getUsableSize());
    }

    @Test
    void execute_shouldThrow_whenBalanceIsInsufficient() {
        CreateOrderRequest request = new CreateOrderRequest(
                "cust-1", "AAPL", OrderSide.BUY, new BigDecimal("2.0000"), new BigDecimal("100.00")
        );
        Asset tryAsset = new Asset("cust-1", "TRY", new BigDecimal("150.0000"), new BigDecimal("150.0000"));

        when(assetRepository.findByCustomerIdAndAssetName("cust-1", "TRY")).thenReturn(Optional.of(tryAsset));

        assertThrows(InsufficientUsableSizeException.class, () -> createOrderUseCase.execute(request));
    }

    @Test
    void execute_shouldThrow_whenCollateralAssetDoesNotExist() {
        CreateOrderRequest request = new CreateOrderRequest(
                "cust-1", "AAPL", OrderSide.BUY, new BigDecimal("1.0000"), new BigDecimal("100.00")
        );
        when(assetRepository.findByCustomerIdAndAssetName("cust-1", "TRY")).thenReturn(Optional.empty());

        assertThrows(AssetNotFoundException.class, () -> createOrderUseCase.execute(request));
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    void execute_shouldThrow_whenAssetNameIsTRY() {
        CreateOrderRequest request = new CreateOrderRequest(
                "cust-1", "TRY", OrderSide.BUY, new BigDecimal("1.00"), new BigDecimal("100.00")
        );

        assertThrows(TryAssetNotAllowedException.class, () -> createOrderUseCase.execute(request));
        verifyNoMoreInteractions(orderRepository);
    }
}
