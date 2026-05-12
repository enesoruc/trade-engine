package com.brokerage.tradeengine.application.usecase;

import com.brokerage.tradeengine.application.dto.response.AssetListItemResponse;
import com.brokerage.tradeengine.application.port.in.ListAssetsInputPort;
import com.brokerage.tradeengine.domain.common.PageableRequest;
import com.brokerage.tradeengine.domain.common.PagedResult;
import com.brokerage.tradeengine.domain.repository.AssetRepository;
import org.springframework.transaction.annotation.Transactional;

public class ListAssetsUseCase implements ListAssetsInputPort {

    private final AssetRepository assetRepository;

    public ListAssetsUseCase(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public PagedResult<AssetListItemResponse> execute(String customerId, int pageNumber, int pageSize) {
        var pagedAssets = assetRepository.findByCustomerId(customerId, PageableRequest.of(pageNumber, pageSize));
        var assets = pagedAssets.content().stream()
                .map(asset -> new AssetListItemResponse(
                        asset.getCustomerId(),
                        asset.getAssetName(),
                        asset.getSize(),
                        asset.getUsableSize()
                ))
                .toList();
        return PagedResult.of(assets, pagedAssets.totalPages(), pagedAssets.totalElements());
    }
}
