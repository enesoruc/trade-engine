package com.brokerage.tradeengine.domain.service;

import com.brokerage.tradeengine.domain.model.Asset;
import com.brokerage.tradeengine.domain.model.Order;
import com.brokerage.tradeengine.domain.model.OrderSide;
import com.brokerage.tradeengine.domain.model.Status;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderBalanceServiceTest {

    private final OrderBalanceService orderBalanceService = new OrderBalanceService();

    @Test
    void reserveCollateralForPendingOrder_shouldReserveFromGivenAsset() {
        Order order = new Order(
                1L, "cust-1", "AAPL", OrderSide.BUY,
                new BigDecimal("2.00"), new BigDecimal("100.00"),
                Status.PENDING, LocalDateTime.now()
        );
        Asset collateral = new Asset("cust-1", "TRY", new BigDecimal("1000.00"), new BigDecimal("1000.00"));

        orderBalanceService.reserveCollateralForPendingOrder(order, collateral);

        assertEquals(new BigDecimal("800.00"), collateral.getUsableSize());
    }

    @Test
    void settleMatchedOrder_shouldSettleBuyOrderBalances() {
        Order order = new Order(
                1L, "cust-1", "AAPL", OrderSide.BUY,
                new BigDecimal("2.00"), new BigDecimal("100.00"),
                Status.PENDING, LocalDateTime.now()
        );
        Asset tryAsset = new Asset("cust-1", "TRY", new BigDecimal("1000.00"), new BigDecimal("800.00"));
        Asset tradedAsset = new Asset("cust-1", "AAPL", new BigDecimal("10.00"), new BigDecimal("10.00"));

        orderBalanceService.settleMatchedOrder(order, tryAsset, tradedAsset);

        assertEquals(new BigDecimal("800.00"), tryAsset.getSize());
        assertEquals(new BigDecimal("800.00"), tryAsset.getUsableSize());
        assertEquals(new BigDecimal("210.00"), tradedAsset.getSize());
        assertEquals(new BigDecimal("210.00"), tradedAsset.getUsableSize());
    }

    @Test
    void settleMatchedOrder_shouldSettleSellOrderBalances() {
        Order order = new Order(
                1L, "cust-1", "AAPL", OrderSide.SELL,
                new BigDecimal("2.00"), new BigDecimal("100.00"),
                Status.PENDING, LocalDateTime.now()
        );
        Asset tryAsset = new Asset("cust-1", "TRY", new BigDecimal("1000.00"), new BigDecimal("1000.00"));
        Asset tradedAsset = new Asset("cust-1", "AAPL", new BigDecimal("300.00"), new BigDecimal("100.00"));

        orderBalanceService.settleMatchedOrder(order, tryAsset, tradedAsset);

        assertEquals(new BigDecimal("1200.00"), tryAsset.getSize());
        assertEquals(new BigDecimal("1200.00"), tryAsset.getUsableSize());
        assertEquals(new BigDecimal("100.00"), tradedAsset.getSize());
        assertEquals(new BigDecimal("100.00"), tradedAsset.getUsableSize());
    }

    @Test
    void releaseCollateralForCanceledOrder_shouldReleaseReservedAmount() {
        Order order = new Order(
                1L, "cust-1", "AAPL", OrderSide.SELL,
                new BigDecimal("2.00"), new BigDecimal("100.00"),
                Status.PENDING, LocalDateTime.now()
        );
        Asset asset = new Asset("cust-1", "AAPL", new BigDecimal("500.00"), new BigDecimal("300.00"));

        orderBalanceService.releaseCollateralForCanceledOrder(order, asset);

        assertEquals(new BigDecimal("500.00"), asset.getUsableSize());
    }
}
