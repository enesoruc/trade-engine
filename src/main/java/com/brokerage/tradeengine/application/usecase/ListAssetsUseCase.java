package com.brokerage.tradeengine.application.usecase;

import com.brokerage.tradeengine.application.dto.response.AssetListItemResponse;
import com.brokerage.tradeengine.domain.repository.AssetRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class ListAssetsUseCase {

    private final AssetRepository assetRepository;

    public ListAssetsUseCase(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    @Transactional(readOnly = true)
    public List<AssetListItemResponse> execute(String customerId) {
        return assetRepository.findByCustomerId(customerId)
                .stream()
                .map(asset -> new AssetListItemResponse(
                        asset.getCustomerId(),
                        asset.getAssetName(),
                        asset.getSize(),
                        asset.getUsableSize()
                ))
                .toList();
    }
}
