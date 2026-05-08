package com.brokerage.tradeengine.infrastructure.adapter.persistence.mapper;

import com.brokerage.tradeengine.domain.model.Asset;
import com.brokerage.tradeengine.infrastructure.adapter.persistence.entity.AssetEntity;
import org.springframework.stereotype.Component;

@Component
public class AssetMapper {

    public Asset toDomain(AssetEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Asset(
                entity.getCustomerId(),
                entity.getAssetName(),
                entity.getSize(),
                entity.getUsableSize()
        );
    }

    public void updateEntity(AssetEntity entity, Asset domain) {
        entity.setCustomerId(domain.getCustomerId());
        entity.setAssetName(domain.getAssetName());
        entity.setSize(domain.getSize());
        entity.setUsableSize(domain.getUsableSize());
    }
}