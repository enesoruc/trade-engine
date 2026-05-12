package com.brokerage.tradeengine.infrastructure.adapter.persistence.repository;

import com.brokerage.tradeengine.infrastructure.adapter.persistence.entity.AssetEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SpringDataAssetRepository extends JpaRepository<AssetEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<AssetEntity> findByCustomer_CustomerIdAndAssetName(String customerId, String assetName);

    Optional<AssetEntity> findReadOnlyByCustomer_CustomerIdAndAssetName(String customerId, String assetName);

    Page<AssetEntity> findByCustomer_CustomerId(String customerId, PageRequest pageRequest);

    List<AssetEntity> findByCustomer_CustomerIdInAndAssetNameIn(Collection<String> customerIds, Collection<String> assetNames);
}
