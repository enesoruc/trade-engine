package com.brokerage.tradeengine.application.port.in;

import com.brokerage.tradeengine.application.dto.response.AssetListItemResponse;

import java.util.List;

public interface ListAssetsInputPort {

    List<AssetListItemResponse> execute(String customerId);
}

