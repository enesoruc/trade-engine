package com.brokerage.tradeengine.infrastructure.adapter.persistence;

import com.brokerage.tradeengine.domain.common.PageableRequest;
import com.brokerage.tradeengine.domain.common.PagedResult;
import com.brokerage.tradeengine.domain.exception.CustomerNotFoundException;
import com.brokerage.tradeengine.domain.model.Asset;
import com.brokerage.tradeengine.domain.repository.AssetRepository;
import com.brokerage.tradeengine.infrastructure.adapter.persistence.entity.AssetEntity;
import com.brokerage.tradeengine.infrastructure.adapter.persistence.mapper.AssetMapper;
import com.brokerage.tradeengine.infrastructure.adapter.persistence.entity.CustomerEntity;
import com.brokerage.tradeengine.infrastructure.adapter.persistence.repository.SpringDataAssetRepository;
import com.brokerage.tradeengine.infrastructure.adapter.persistence.repository.SpringDataCustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AssetRepositoryAdapter implements AssetRepository {

    private final SpringDataAssetRepository springDataAssetRepository;
    private final SpringDataCustomerRepository springDataCustomerRepository;
    private final AssetMapper assetMapper;

    @Override
    public Optional<Asset> findByCustomerIdAndAssetName(String customerId, String assetName) {
        return springDataAssetRepository.findByCustomer_CustomerIdAndAssetName(customerId, assetName)
                .map(assetMapper::toDomain);
    }

    @Override
    public PagedResult<Asset> findByCustomerId(String customerId, PageableRequest pageableRequest) {
        var pageRequest = PageRequest.of(pageableRequest.pageNumber(), pageableRequest.pageSize());
        var pageableAssets = springDataAssetRepository.findByCustomer_CustomerId(customerId, pageRequest);
        var assets = pageableAssets
                .stream()
                .map(assetMapper::toDomain)
                .toList();
        return PagedResult.of(assets, pageableAssets.getTotalPages(), pageableAssets.getTotalElements());
    }

    @Override
    public Asset save(Asset asset) {
        return saveAll(List.of(asset)).getFirst();
    }

    @Override
    public List<Asset> saveAll(List<Asset> assets) {
        if (assets.isEmpty()) {
            return List.of();
        }

        Map<String, CustomerEntity> customersById = fetchCustomers(assets);
        Map<String, AssetEntity> existingEntitiesByKey = fetchExistingAssets(assets);

        List<AssetEntity> entitiesToSave = assets.stream()
                .map(asset -> {
                    CustomerEntity customer = customersById.get(asset.getCustomerId());
                    if (customer == null) {
                        throw new CustomerNotFoundException(asset.getCustomerId());
                    }

                    AssetEntity entity = existingEntitiesByKey.getOrDefault(assetKey(asset.getCustomerId(), asset.getAssetName()), new AssetEntity());
                    assetMapper.updateEntity(entity, asset, customer);
                    return entity;
                })
                .toList();

        return springDataAssetRepository.saveAll(entitiesToSave)
                .stream()
                .map(assetMapper::toDomain)
                .toList();
    }

    private Map<String, CustomerEntity> fetchCustomers(List<Asset> assets) {
        List<String> customerIds = assets.stream()
                .map(Asset::getCustomerId)
                .distinct()
                .toList();

        return springDataCustomerRepository.findByCustomerIdIn(customerIds)
                .stream()
                .collect(Collectors.toMap(CustomerEntity::getCustomerId, Function.identity()));
    }

    private Map<String, AssetEntity> fetchExistingAssets(List<Asset> assets) {
        List<String> customerIds = assets.stream()
                .map(Asset::getCustomerId)
                .distinct()
                .toList();
        List<String> assetNames = assets.stream()
                .map(Asset::getAssetName)
                .distinct()
                .toList();

        return springDataAssetRepository.findByCustomer_CustomerIdInAndAssetNameIn(customerIds, assetNames)
                .stream()
                .collect(Collectors.toMap(
                        entity -> assetKey(entity.getCustomer().getCustomerId(), entity.getAssetName()),
                        Function.identity(),
                        (left, right) -> left
                ));
    }

    private String assetKey(String customerId, String assetName) {
        return customerId + "|" + assetName;
    }
}
