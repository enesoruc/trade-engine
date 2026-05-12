package com.brokerage.tradeengine.domain.repository;

import com.brokerage.tradeengine.domain.common.PagedResult;
import com.brokerage.tradeengine.domain.model.Order;
import com.brokerage.tradeengine.domain.model.OrderSide;
import com.brokerage.tradeengine.domain.model.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Optional<Order> findById(Long id);
    List<Order> findPendingOrders(int limit);
    PagedResult<Order> findByFilters(
            String customerId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Status status,
            OrderSide orderSide,
            String assetName,
            Integer pageNumber,
            Integer pageSize
    );
    void save(Order order);
    void saveAll(List<Order> orders);
}
