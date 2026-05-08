package com.brokerage.tradeengine.domain.repository;

import com.brokerage.tradeengine.domain.model.Asset;

import java.util.List;
import java.util.Optional;

public interface AssetRepository {
    Optional<Asset> findByCustomerIdAndAssetName(String customerId, String assetName);
    List<Asset> findByCustomerId(String customerId);
    Asset save(Asset asset);
}
