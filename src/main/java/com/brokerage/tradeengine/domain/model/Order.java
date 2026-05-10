package com.brokerage.tradeengine.domain.model;

import com.brokerage.tradeengine.domain.constant.AssetConstants;
import com.brokerage.tradeengine.domain.exception.BlankFieldException;
import com.brokerage.tradeengine.domain.exception.OrderNotPendingForCancelException;
import com.brokerage.tradeengine.domain.exception.OrderNotPendingForMatchException;
import com.brokerage.tradeengine.domain.exception.PositiveValueRequiredException;
import com.brokerage.tradeengine.domain.exception.TryAssetNotAllowedException;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
public class Order {

    private final Long id;
    private final String customerId;
    private final String assetName;
    private final OrderSide orderSide;
    private final BigDecimal size;
    private final BigDecimal price;
    private Status status;
    private final LocalDateTime createDate;

    public Order(
            Long id,
            String customerId,
            String assetName,
            OrderSide orderSide,
            BigDecimal size,
            BigDecimal price,
            Status status,
            LocalDateTime createDate
    ) {
        this.id = id;
        this.customerId = requireText(customerId, "customerId");
        this.assetName = requireText(assetName, "assetName");
        this.orderSide = Objects.requireNonNull(orderSide, "orderSide cannot be null");
        this.size = normalizeSize(size);
        this.price = normalizePrice(price);
        this.status = Objects.requireNonNull(status, "status cannot be null");
        this.createDate = Objects.requireNonNull(createDate, "createDate cannot be null");
    }

    public Order(
            String customerId,
            String assetName,
            OrderSide orderSide,
            BigDecimal size,
            BigDecimal price
    ) {
        this(null, customerId, assetName, orderSide, size, price, Status.PENDING, LocalDateTime.now());
    }

    public void cancel() {
        if (status != Status.PENDING) {
            throw new OrderNotPendingForCancelException();
        }
        this.status = Status.CANCELED;
    }

    public void match() {
        if (status != Status.PENDING) {
            throw new OrderNotPendingForMatchException();
        }
        this.status = Status.MATCHED;
    }

    public BigDecimal calculateTradeValue() {
        return normalizePrice(size.multiply(price));
    }

    public String collateralAssetName() {
        return orderSide == OrderSide.BUY ? AssetSymbol.TRY.name() : assetName;
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new BlankFieldException(fieldName);
        }
        String trimmed = value.trim();
        if ("assetName".equals(fieldName) && AssetSymbol.TRY.name().equalsIgnoreCase(trimmed)) {
            throw new TryAssetNotAllowedException();
        }
        return trimmed;
    }

    private static BigDecimal normalizeSize(BigDecimal value) {
        return validateAndScale(value, "size");
    }

    private static BigDecimal normalizePrice(BigDecimal value) {
        return validateAndScale(value, "price");
    }

    private static BigDecimal validateAndScale(BigDecimal value, String fieldName) {
        if (value.signum() <= 0) {
            throw new PositiveValueRequiredException(fieldName);
        }
        return value.setScale(AssetConstants.ASSET_SCALE, RoundingMode.HALF_UP);
    }
}
