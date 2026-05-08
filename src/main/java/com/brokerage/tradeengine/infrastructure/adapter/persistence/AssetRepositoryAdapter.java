package com.brokerage.tradeengine.infrastructure.adapter.persistence;

import com.brokerage.tradeengine.domain.model.Asset;
import com.brokerage.tradeengine.domain.repository.AssetRepository;
import com.brokerage.tradeengine.infrastructure.adapter.persistence.entity.AssetEntity;
import com.brokerage.tradeengine.infrastructure.adapter.persistence.mapper.AssetMapper;
import com.brokerage.tradeengine.infrastructure.adapter.persistence.repository.SpringDataAssetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AssetRepositoryAdapter implements AssetRepository {

    private final SpringDataAssetRepository springDataAssetRepository;
    private final AssetMapper assetMapper;

    @Override
    public Optional<Asset> findByCustomerIdAndAssetName(String customerId, String assetName) {
        return springDataAssetRepository.findByCustomerIdAndAssetName(customerId, assetName)
                .map(assetMapper::toDomain);
    }

    @Override
    public List<Asset> findByCustomerId(String customerId) {
        return springDataAssetRepository.findByCustomerId(customerId)
                .stream()
                .map(assetMapper::toDomain)
                .toList();
    }

    @Override
    public Asset save(Asset asset) {
        AssetEntity entity = springDataAssetRepository
                .findByCustomerIdAndAssetName(asset.getCustomerId(), asset.getAssetName())
                .orElseGet(AssetEntity::new);

        assetMapper.updateEntity(entity, asset);

        return assetMapper.toDomain(springDataAssetRepository.save(entity));
    }
}
