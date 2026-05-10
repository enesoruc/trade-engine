package com.brokerage.tradeengine.application.usecase;

import com.brokerage.tradeengine.application.dto.mapper.CreateOrderResponseMapper;
import com.brokerage.tradeengine.application.dto.request.CreateOrderRequest;
import com.brokerage.tradeengine.application.dto.response.CreateOrderResponse;
import com.brokerage.tradeengine.application.port.in.CreateOrderInputPort;
import com.brokerage.tradeengine.domain.exception.AssetNotFoundException;
import com.brokerage.tradeengine.domain.model.Asset;
import com.brokerage.tradeengine.domain.model.AssetSymbol;
import com.brokerage.tradeengine.domain.model.Order;
import com.brokerage.tradeengine.domain.repository.AssetRepository;
import com.brokerage.tradeengine.domain.repository.OrderRepository;
import com.brokerage.tradeengine.domain.service.OrderBalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
public class CreateOrderUseCase implements CreateOrderInputPort {

    private final AssetRepository assetRepository;
    private final OrderRepository orderRepository;
    private final CreateOrderResponseMapper mapper;
    private final OrderBalanceService orderBalanceService;

    @Transactional
    @Override
    public CreateOrderResponse execute(CreateOrderRequest request) {
        log.info("Create Order Request received: {}", request);

        Order order = new Order(
                request.customerId(),
                request.assetName(),
                request.side(),
                request.size(),
                request.price()
        );
        log.info("Processing create order request for customer: {}, asset: {}, side: {}",
                order.getCustomerId(), order.getAssetName(), order.getOrderSide());

        Asset collateral = findAsset(order.getCustomerId(), order.collateralAssetName());
        orderBalanceService.reserveCollateralForPendingOrder(order, collateral);
        assetRepository.save(collateral);
        log.debug("Collateral asset updated: {}", collateral);

        Order savedOrder = orderRepository.save(order);
        log.info("Order saved successfully. Order details: {}", savedOrder);

        Asset tryAsset = findAsset(request.customerId(), AssetSymbol.TRY.name());
        return mapper.map(savedOrder, tryAsset.getUsableSize());
    }

    private Asset findAsset(String customerId, String assetName) {
        return assetRepository.findByCustomerIdAndAssetName(customerId, assetName)
                .orElseThrow(() -> new AssetNotFoundException(assetName));
    }
}
