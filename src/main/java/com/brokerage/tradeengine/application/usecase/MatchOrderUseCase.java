package com.brokerage.tradeengine.application.usecase;

import com.brokerage.tradeengine.application.port.in.MatchOrderInputPort;
import com.brokerage.tradeengine.domain.exception.AssetNotFoundException;
import com.brokerage.tradeengine.domain.model.Asset;
import com.brokerage.tradeengine.domain.model.AssetSymbol;
import com.brokerage.tradeengine.domain.model.Order;
import com.brokerage.tradeengine.domain.repository.AssetRepository;
import com.brokerage.tradeengine.domain.repository.OrderRepository;
import com.brokerage.tradeengine.domain.service.OrderBalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Slf4j
public class MatchOrderUseCase implements MatchOrderInputPort {

    private static final int BATCH_SIZE = 500;
    private static final String TRY_ASSET = AssetSymbol.TRY.name();

    private final OrderRepository orderRepository;
    private final AssetRepository assetRepository;
    private final OrderBalanceService orderBalanceService;
    private final TransactionTemplate transactionTemplate;

    @Override
    public void execute() {
        log.info("Starting order matching process.");
        while (true) {
            List<Order> pendingOrders = orderRepository.findPendingOrders(BATCH_SIZE);
            if (pendingOrders.isEmpty()) {
                log.info("No pending orders found. Order matching process finished.");
                return;
            }
            log.info("Found {} pending orders for matching in this batch.", pendingOrders.size());

            transactionTemplate.executeWithoutResult(ignored -> matchAndSettleBatch(pendingOrders));
            log.info("Successfully matched and settled {} orders in this batch.", pendingOrders.size());
        }
    }

    private void matchAndSettleBatch(List<Order> pendingOrders) {
        Map<String, Asset> assetCache = new HashMap<>();
        Set<String> touchedAssetKeys = new HashSet<>();
        List<Asset> touchedAssets = new ArrayList<>();

        for (Order order : pendingOrders) {
            order.match();
            Asset tryAsset = requireTryAsset(order.getCustomerId(), assetCache);
            Asset tradedAsset = findOrInitializeAsset(order.getCustomerId(), order.getAssetName(), assetCache);
            orderBalanceService.settleMatchedOrder(order, tryAsset, tradedAsset);
            markForSave(tryAsset, touchedAssetKeys, touchedAssets);
            markForSave(tradedAsset, touchedAssetKeys, touchedAssets);
        }

        assetRepository.saveAll(touchedAssets);
        orderRepository.saveAll(pendingOrders);
    }

    private void markForSave(Asset asset, Set<String> touchedAssetKeys, List<Asset> touchedAssets) {
        String key = assetKey(asset.getCustomerId(), asset.getAssetName());
        if (touchedAssetKeys.add(key)) {
            touchedAssets.add(asset);
        }
    }

    private Asset requireTryAsset(String customerId, Map<String, Asset> assetCache) {
        String key = assetKey(customerId, TRY_ASSET);
        return assetCache.computeIfAbsent(
                key,
                ignored -> assetRepository.findByCustomerIdAndAssetName(customerId, TRY_ASSET)
                        .orElseThrow(() -> new AssetNotFoundException(TRY_ASSET))
        );
    }

    private Asset findOrInitializeAsset(String customerId, String assetName, Map<String, Asset> assetCache) {
        String key = assetKey(customerId, assetName);
        return assetCache.computeIfAbsent(
                key,
                ignored -> assetRepository.findByCustomerIdAndAssetName(customerId, assetName)
                        .orElseGet(() -> Asset.initialize(customerId, assetName))
        );
    }

    private String assetKey(String customerId, String assetName) {
        return customerId + "|" + assetName;
    }
}
