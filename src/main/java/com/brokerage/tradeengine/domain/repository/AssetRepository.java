package com.brokerage.tradeengine.domain.repository;

import com.brokerage.tradeengine.domain.common.PageableRequest;
import com.brokerage.tradeengine.domain.common.PagedResult;
import com.brokerage.tradeengine.domain.model.Asset;

import java.util.List;
import java.util.Optional;

public interface AssetRepository {
    Optional<Asset> findByCustomerIdAndAssetName(String customerId, String assetName);
    PagedResult<Asset> findByCustomerId(String customerId, PageableRequest pageRequest);
    Asset save(Asset asset);
    List<Asset> saveAll(List<Asset> assets);
}
