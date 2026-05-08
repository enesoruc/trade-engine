package com.brokerage.tradeengine.domain.model;

import com.brokerage.tradeengine.domain.exception.BlankFieldException;
import com.brokerage.tradeengine.domain.exception.InsufficientUsableSizeException;
import com.brokerage.tradeengine.domain.exception.NonNegativeValueRequiredException;
import com.brokerage.tradeengine.domain.exception.UsableSizeExceedsSizeException;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import static com.brokerage.tradeengine.domain.constant.AssetConstants.ASSET_SCALE;

@Getter
public class Asset {

    private final String customerId;
    private final String assetName;
    private BigDecimal size;
    private BigDecimal usableSize;

    public Asset(String customerId, String assetName, BigDecimal size, BigDecimal usableSize) {
        this.customerId = requireText(customerId, "customerId");
        this.assetName = requireText(assetName, "assetName");
        this.size = normalizeAssetValue(size, "size");
        this.usableSize = normalizeAssetValue(usableSize, "usableSize");

        if (this.usableSize.compareTo(this.size) > 0) {
            throw new UsableSizeExceedsSizeException();
        }
    }

    public void reserveUsableSize(BigDecimal amount) {
        BigDecimal normalizedAmount = normalizeAssetValue(amount, "amount");
        if (usableSize.compareTo(normalizedAmount) < 0) {
            throw new InsufficientUsableSizeException();
        }
        usableSize = usableSize.subtract(normalizedAmount).setScale(ASSET_SCALE, RoundingMode.HALF_UP);
    }

    public void releaseReservedUsableSize(BigDecimal amount) {
        BigDecimal normalizedAmount = normalizeAssetValue(amount, "amount");
        BigDecimal updatedUsableSize = usableSize.add(normalizedAmount).setScale(ASSET_SCALE, RoundingMode.HALF_UP);
        if (updatedUsableSize.compareTo(size) > 0) {
            throw new UsableSizeExceedsSizeException();
        }
        usableSize = updatedUsableSize;
    }

    public void decreaseSize(BigDecimal amount) {
        BigDecimal normalizedAmount = normalizeAssetValue(amount, "amount");
        BigDecimal updatedSize = size.subtract(normalizedAmount).setScale(ASSET_SCALE, RoundingMode.HALF_UP);
        if (updatedSize.signum() < 0) {
            throw new NonNegativeValueRequiredException("size");
        }
        if (usableSize.compareTo(updatedSize) > 0) {
            throw new UsableSizeExceedsSizeException();
        }
        size = updatedSize;
    }

    public void increaseSizeAndUsableSize(BigDecimal amount) {
        BigDecimal normalizedAmount = normalizeAssetValue(amount, "amount");
        size = size.add(normalizedAmount).setScale(ASSET_SCALE, RoundingMode.HALF_UP);
        usableSize = usableSize.add(normalizedAmount).setScale(ASSET_SCALE, RoundingMode.HALF_UP);
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new BlankFieldException(fieldName);
        }
        return value;
    }

    private static BigDecimal normalizeAssetValue(BigDecimal value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " cannot be null");
        if (value.signum() < 0) {
            throw new NonNegativeValueRequiredException(fieldName);
        }
        return value.setScale(ASSET_SCALE, RoundingMode.HALF_UP);
    }
}
