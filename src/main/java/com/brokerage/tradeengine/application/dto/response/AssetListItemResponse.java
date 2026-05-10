package com.brokerage.tradeengine.application.dto.response;

import java.math.BigDecimal;

public record AssetListItemResponse(
        String customerId,
        String assetName,
        BigDecimal size,
        BigDecimal usableSize
) {
}
