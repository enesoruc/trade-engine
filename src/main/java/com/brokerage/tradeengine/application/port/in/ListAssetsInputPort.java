package com.brokerage.tradeengine.application.port.in;

import com.brokerage.tradeengine.application.dto.response.AssetListItemResponse;
import com.brokerage.tradeengine.domain.common.PagedResult;

public interface ListAssetsInputPort {

    PagedResult<AssetListItemResponse> execute(String customerId, int pageNumber, int pageSize);
}

