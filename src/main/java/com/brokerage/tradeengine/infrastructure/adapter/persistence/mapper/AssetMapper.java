package com.brokerage.tradeengine.infrastructure.adapter.persistence.mapper;

import com.brokerage.tradeengine.domain.model.Asset;
import com.brokerage.tradeengine.infrastructure.adapter.persistence.entity.AssetEntity;
import com.brokerage.tradeengine.infrastructure.adapter.persistence.entity.CustomerEntity;
import org.springframework.stereotype.Component;

@Component
public class AssetMapper {

    public Asset toDomain(AssetEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Asset(
                entity.getCustomer().getCustomerId(),
                entity.getAssetName(),
                entity.getSize(),
                entity.getUsableSize()
        );
    }

    public void updateEntity(AssetEntity entity, Asset domain, CustomerEntity customer) {
        entity.setCustomer(customer);
        entity.setAssetName(domain.getAssetName());
        entity.setSize(domain.getSize());
        entity.setUsableSize(domain.getUsableSize());
    }
}