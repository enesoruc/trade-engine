package com.brokerage.tradeengine.domain.model;

import com.brokerage.tradeengine.domain.exception.OrderNotPendingForCancelException;
import com.brokerage.tradeengine.domain.exception.OrderNotPendingForMatchException;
import com.brokerage.tradeengine.domain.exception.PositiveValueRequiredException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderTest {

    @Test
    void cancel_shouldMovePendingOrderToCanceled() {
        Order order = new Order(
                1L, "cust-1", "AAPL", OrderSide.SELL,
                new BigDecimal("2.50"), new BigDecimal("150.25"),
                Status.PENDING, LocalDateTime.now()
        );

        order.cancel();

        assertEquals(Status.CANCELED, order.getStatus());
    }

    @Test
    void cancel_shouldThrow_whenOrderIsNotPending() {
        Order order = new Order(
                1L, "cust-1", "AAPL", OrderSide.SELL,
                new BigDecimal("2.50"), new BigDecimal("150.25"),
                Status.MATCHED, LocalDateTime.now()
        );

        assertThrows(OrderNotPendingForCancelException.class, order::cancel);
    }

    @Test
    void match_shouldMovePendingOrderToMatched() {
        Order order = new Order(
                1L, "cust-1", "AAPL", OrderSide.BUY,
                new BigDecimal("1.00"), new BigDecimal("99.99"),
                Status.PENDING, LocalDateTime.now()
        );

        order.match();

        assertEquals(Status.MATCHED, order.getStatus());
    }

    @Test
    void calculateTradeValue_shouldEqualSizeTimesPrice_forBuy() {
        Order order = new Order(
                1L, "cust-1", "AAPL", OrderSide.BUY,
                new BigDecimal("10.00"), new BigDecimal("25.50"),
                Status.PENDING, LocalDateTime.now()
        );

        assertEquals(new BigDecimal("255.00"), order.calculateTradeValue());
    }

    @Test
    void calculateTradeValue_shouldBeSizeTimesPrice_forSell() {
        Order order = new Order(
                1L, "cust-1", "AAPL", OrderSide.SELL,
                new BigDecimal("3.00"), new BigDecimal("10.00"),
                Status.PENDING, LocalDateTime.now()
        );

        assertEquals(new BigDecimal("30.00"), order.calculateTradeValue());
    }

    @Test
    void collateralAssetName_shouldBeTry_forBuy() {
        Order order = new Order(
                1L, "cust-1", "AAPL", OrderSide.BUY,
                new BigDecimal("1.00"), new BigDecimal("10.00"),
                Status.PENDING, LocalDateTime.now()
        );

        assertEquals(AssetSymbol.TRY.name(), order.collateralAssetName());
    }

    @Test
    void collateralAssetName_shouldBeInstrument_forSell() {
        Order order = new Order(
                1L, "cust-1", "AAPL", OrderSide.SELL,
                new BigDecimal("1.00"), new BigDecimal("10.00"),
                Status.PENDING, LocalDateTime.now()
        );

        assertEquals("AAPL", order.collateralAssetName());
    }

    @Test
    void constructor_shouldNormalizePriceAndSizeScale() {
        Order order = new Order(
                1L, "cust-1", "AAPL", OrderSide.BUY,
                new BigDecimal("1.23456"), new BigDecimal("99.999"),
                Status.PENDING, LocalDateTime.now()
        );

        assertEquals(new BigDecimal("1.23"), order.getSize());
        assertEquals(new BigDecimal("100.00"), order.getPrice());
    }

    @Test
    void match_shouldThrow_whenOrderIsNotPending() {
        Order order = new Order(
                1L, "cust-1", "AAPL", OrderSide.BUY,
                new BigDecimal("1.00"), new BigDecimal("99.99"),
                Status.CANCELED, LocalDateTime.now()
        );

        assertThrows(OrderNotPendingForMatchException.class, order::match);
    }

    @Test
    void constructor_shouldThrow_whenSizeIsNonPositive() {
        assertThrows(
                PositiveValueRequiredException.class,
                () -> new Order(
                        1L, "cust-1", "AAPL", OrderSide.BUY,
                        BigDecimal.ZERO, new BigDecimal("100.00"),
                        Status.PENDING, LocalDateTime.now()
                )
        );
    }
}
