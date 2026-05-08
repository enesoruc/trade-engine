package com.brokerage.tradeengine.infrastructure.adapter.persistence;

import com.brokerage.tradeengine.domain.model.Order;
import com.brokerage.tradeengine.domain.model.OrderSide;
import com.brokerage.tradeengine.domain.model.Status;
import com.brokerage.tradeengine.domain.repository.OrderRepository;
import com.brokerage.tradeengine.infrastructure.adapter.persistence.entity.OrderEntity;
import com.brokerage.tradeengine.infrastructure.adapter.persistence.repository.SpringDataOrderRepository;
import com.brokerage.tradeengine.infrastructure.adapter.persistence.specification.OrderSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements OrderRepository {

    private final SpringDataOrderRepository springDataOrderRepository;

    @Override
    public Optional<Order> findById(Long id) {
        return springDataOrderRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Order> findByFilters(
            String customerId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Status status,
            OrderSide orderSide,
            String assetName
    ) {
        return springDataOrderRepository.findAll(
                        OrderSpecifications.byFilters(customerId, startDate, endDate, status, orderSide, assetName)
                )
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Order save(Order order) {
        OrderEntity entity = new OrderEntity();
        if (order.getId() != null) {
            entity.setId(order.getId());
        }
        entity.setCustomerId(order.getCustomerId());
        entity.setAssetName(order.getAssetName());
        entity.setOrderSide(order.getOrderSide());
        entity.setSize(order.getSize());
        entity.setPrice(order.getPrice());
        entity.setStatus(order.getStatus());
        entity.setCreateDate(order.getCreateDate());

        return toDomain(springDataOrderRepository.save(entity));
    }

    private Order toDomain(OrderEntity entity) {
        return new Order(
                entity.getId(),
                entity.getCustomerId(),
                entity.getAssetName(),
                entity.getOrderSide(),
                entity.getSize(),
                entity.getPrice(),
                entity.getStatus(),
                entity.getCreateDate()
        );
    }
}
