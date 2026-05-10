package com.brokerage.tradeengine.domain.service;

import com.brokerage.tradeengine.domain.model.Asset;
import com.brokerage.tradeengine.domain.model.Order;
import com.brokerage.tradeengine.domain.model.OrderSide;

import java.math.BigDecimal;

public final class OrderBalanceService {

    public void reserveCollateralForPendingOrder(Order order, Asset collateralAsset) {
        collateralAsset.reserveUsableSize(order.calculateTradeValue());
    }

    public void settleMatchedOrder(Order order, Asset tryAsset, Asset tradedAsset) {
        BigDecimal tradeValueTry = order.calculateTradeValue();
        if (order.getOrderSide() == OrderSide.BUY) {
            tryAsset.releaseReservedUsableSize(tradeValueTry);
            tryAsset.decreaseSizeAndUsableSize(tradeValueTry);
            tradedAsset.increaseSizeAndUsableSize(tradeValueTry);
        } else {
            tradedAsset.releaseReservedUsableSize(tradeValueTry);
            tradedAsset.decreaseSizeAndUsableSize(tradeValueTry);
            tryAsset.increaseSizeAndUsableSize(tradeValueTry);
        }
    }

    public void releaseCollateralForCanceledOrder(Order order, Asset releasableAsset) {
        releasableAsset.releaseReservedUsableSize(order.calculateTradeValue());
    }
}
