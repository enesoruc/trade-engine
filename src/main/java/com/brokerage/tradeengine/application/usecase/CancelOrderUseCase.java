package com.brokerage.tradeengine.application.usecase;

import com.brokerage.tradeengine.application.dto.mapper.OrderItemResponseMapper;
import com.brokerage.tradeengine.application.dto.request.CancelOrderRequest;
import com.brokerage.tradeengine.application.dto.response.OrderItemResponse;
import com.brokerage.tradeengine.domain.exception.AssetNotFoundException;
import com.brokerage.tradeengine.application.exception.OrderNotFoundException;
import com.brokerage.tradeengine.application.exception.OrderOwnershipViolationException;
import com.brokerage.tradeengine.application.port.in.CancelOrderInputPort;
import com.brokerage.tradeengine.domain.model.Asset;
import com.brokerage.tradeengine.domain.model.Order;
import com.brokerage.tradeengine.domain.repository.AssetRepository;
import com.brokerage.tradeengine.domain.repository.OrderRepository;
import com.brokerage.tradeengine.domain.service.OrderBalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CancelOrderUseCase implements CancelOrderInputPort {
    
    private final OrderRepository orderRepository;
    private final AssetRepository assetRepository;
    private final OrderItemResponseMapper orderMapper;
    private final OrderBalanceService orderBalanceService;

    @Transactional
    @Override
    public OrderItemResponse execute(CancelOrderRequest request) {
        log.info("Cancel Order Request received for order ID: {} by customer: {}", request.orderId(), request.customerId());

        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new OrderNotFoundException(request.orderId()));

        validateOwnership(order, request.customerId());
        log.info("Order ownership validated for order ID: {} and customer: {}", request.orderId(), request.customerId());

        order.cancel();
        log.debug("Order status set to cancelled for order ID: {}", order.getId());

        releaseReservedBalance(order);

        Order savedOrder = orderRepository.save(order);
        log.info("Order cancelled and saved successfully. Cancelled order details: {}", savedOrder);
        return orderMapper.map(savedOrder);
    }

    private void validateOwnership(Order order, String customerId) {
        if (!order.getCustomerId().equals(customerId)) {
            log.error("Customer {} attempted to cancel order {} which belongs to customer {}", customerId, order.getId(), order.getCustomerId());
            throw new OrderOwnershipViolationException(customerId);
        }
    }

    private void releaseReservedBalance(Order order) {
        String releasableAssetName = order.collateralAssetName();
        log.debug("Attempting to release reserved balance for asset: {} for order ID: {}", releasableAssetName, order.getId());

        Asset releasableAsset = assetRepository
                .findByCustomerIdAndAssetName(order.getCustomerId(), releasableAssetName)
                .orElseThrow(() -> new AssetNotFoundException(releasableAssetName));

        orderBalanceService.releaseCollateralForCanceledOrder(order, releasableAsset);
        assetRepository.save(releasableAsset);
        log.info("Reserved balance released for asset {} for order ID: {}. Updated asset details: {}", releasableAssetName, order.getId(), releasableAsset);
    }
}
