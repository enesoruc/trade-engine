package com.brokerage.tradeengine.domain.model;

import com.brokerage.tradeengine.domain.exception.InsufficientUsableSizeException;
import com.brokerage.tradeengine.domain.exception.UsableSizeExceedsSizeException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AssetTest {

    @Test
    void reserveUsableSize_shouldDecreaseUsableBalance() {
        Asset asset = new Asset("cust-1", "TRY", new BigDecimal("1000.00"), new BigDecimal("1000.00"));

        asset.reserveUsableSize(new BigDecimal("250.12"));

        assertEquals(new BigDecimal("749.88"), asset.getUsableSize());
        assertEquals(new BigDecimal("1000.00"), asset.getSize());
    }

    @Test
    void reserveUsableSize_shouldThrow_whenAmountExceedsUsableSize() {
        Asset asset = new Asset("cust-1", "TRY", new BigDecimal("100.00"), new BigDecimal("50.00"));

        InsufficientUsableSizeException ex = assertThrows(
                InsufficientUsableSizeException.class,
                () -> asset.reserveUsableSize(new BigDecimal("60.00"))
        );

        assertEquals("insufficient usableSize to block", ex.getMessage());
    }

    @Test
    void releaseReservedUsableSize_shouldIncreaseUsableBalanceWithinSizeLimit() {
        Asset asset = new Asset("cust-1", "AAPL", new BigDecimal("10.00"), new BigDecimal("7.00"));

        asset.releaseReservedUsableSize(new BigDecimal("2.50"));

        assertEquals(new BigDecimal("9.50"), asset.getUsableSize());
        assertEquals(new BigDecimal("10.00"), asset.getSize());
    }

    @Test
    void decreaseSizeAndUsableSize_shouldSubtractSameAmountFromSizeAndUsable() {
        Asset asset = new Asset("cust-1", "TRY", new BigDecimal("1000.00"), new BigDecimal("900.00"));

        asset.decreaseSizeAndUsableSize(new BigDecimal("100.00"));

        assertEquals(new BigDecimal("900.00"), asset.getSize());
        assertEquals(new BigDecimal("800.00"), asset.getUsableSize());
    }

    @Test
    void releaseReservedUsableSize_shouldThrow_whenUsableExceedsSize() {
        Asset asset = new Asset("cust-1", "AAPL", new BigDecimal("10.00"), new BigDecimal("9.00"));

        UsableSizeExceedsSizeException ex = assertThrows(
                UsableSizeExceedsSizeException.class,
                () -> asset.releaseReservedUsableSize(new BigDecimal("1.50"))
        );

        assertEquals("usableSize cannot be greater than size", ex.getMessage());
    }
}
