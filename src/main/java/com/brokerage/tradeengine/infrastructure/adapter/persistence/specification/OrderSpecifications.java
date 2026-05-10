package com.brokerage.tradeengine.infrastructure.adapter.persistence.specification;

import com.brokerage.tradeengine.domain.model.OrderSide;
import com.brokerage.tradeengine.domain.model.Status;
import com.brokerage.tradeengine.infrastructure.adapter.persistence.entity.OrderEntity;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Stream;

public final class OrderSpecifications {

    private OrderSpecifications() {
    }

    public static Specification<OrderEntity> byFilters(
            String customerId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Status status,
            OrderSide orderSide,
            String assetName
    ) {
        return Stream.of(
                        hasCustomerId(customerId),
                        hasCreateDateBetween(startDate, endDate),
                        hasStatus(status),
                        hasOrderSide(orderSide),
                        hasAssetName(assetName))
                .filter(Objects::nonNull)
                .reduce(Specification::and)
                .orElse((root, query, criteriaBuilder) -> criteriaBuilder.conjunction());
    }

    private static Specification<OrderEntity> hasCustomerId(String customerId) {
        if (customerId == null || customerId.isBlank()) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.join("customer", JoinType.INNER).get("customerId"),
                        customerId);
    }

    private static Specification<OrderEntity> hasCreateDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("createDate"), startDate, endDate);
    }

    private static Specification<OrderEntity> hasStatus(Status status) {
        if (status == null) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), status);
    }

    private static Specification<OrderEntity> hasOrderSide(OrderSide orderSide) {
        if (orderSide == null) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("orderSide"), orderSide);
    }

    private static Specification<OrderEntity> hasAssetName(String assetName) {
        if (assetName == null || assetName.isBlank()) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(criteriaBuilder.lower(root.get("assetName")), assetName.toLowerCase());
    }
}
