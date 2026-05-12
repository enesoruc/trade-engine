package com.brokerage.tradeengine.infrastructure.adapter.persistence;

import com.brokerage.tradeengine.domain.common.PagedResult;
import com.brokerage.tradeengine.domain.exception.CustomerNotFoundException;
import com.brokerage.tradeengine.domain.model.Order;
import com.brokerage.tradeengine.domain.model.OrderSide;
import com.brokerage.tradeengine.domain.model.Status;
import com.brokerage.tradeengine.domain.repository.OrderRepository;
import com.brokerage.tradeengine.infrastructure.adapter.persistence.entity.CustomerEntity;
import com.brokerage.tradeengine.infrastructure.adapter.persistence.entity.OrderEntity;
import com.brokerage.tradeengine.infrastructure.adapter.persistence.repository.SpringDataCustomerRepository;
import com.brokerage.tradeengine.infrastructure.adapter.persistence.repository.SpringDataOrderRepository;
import com.brokerage.tradeengine.infrastructure.adapter.persistence.specification.OrderSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements OrderRepository {

    private final SpringDataOrderRepository springDataOrderRepository;
    private final SpringDataCustomerRepository springDataCustomerRepository;

    @Override
    public Optional<Order> findById(Long id) {
        return springDataOrderRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Order> findPendingOrders(int limit) {
        return springDataOrderRepository.findByStatusOrderByCreateDateAscIdAsc(Status.PENDING, PageRequest.of(0, limit))
                .getContent()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public PagedResult<Order> findByFilters(
            String customerId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Status status,
            OrderSide orderSide,
            String assetName,
            Integer pageNumber,
            Integer pageSize
    ) {
        var pageRequest = PageRequest.of(pageNumber, pageSize);
        var pageableOrders = springDataOrderRepository.findAll(
                OrderSpecifications.byFilters(customerId, startDate, endDate, status, orderSide, assetName), pageRequest);
        var orders = pageableOrders.getContent()
                .stream()
                .map(this::toDomain)
                .toList();
        return PagedResult.of(orders, pageableOrders.getTotalPages(), pageableOrders.getTotalElements());
    }

    @Override
    public void save(Order order) {
        saveAll(List.of(order));
    }

    @Override
    public void saveAll(List<Order> orders) {
        if (orders.isEmpty()) {
            return;
        }

        Map<String, CustomerEntity> customersById = springDataCustomerRepository.findByCustomerIdIn(
                        orders.stream().map(Order::getCustomerId).distinct().toList()
                )
                .stream()
                .collect(Collectors.toMap(CustomerEntity::getCustomerId, Function.identity()));

        List<OrderEntity> entities = orders.stream()
                .map(order -> toEntity(order, customersById))
                .toList();

        springDataOrderRepository.saveAll(entities);
    }

    private OrderEntity toEntity(Order order, Map<String, CustomerEntity> customersById) {
        CustomerEntity customer = customersById.get(order.getCustomerId());
        if (customer == null) {
            throw new CustomerNotFoundException(order.getCustomerId());
        }

        OrderEntity entity = new OrderEntity();
        if (order.getId() != null) {
            entity.setId(order.getId());
        }
        entity.setCustomer(customer);
        entity.setAssetName(order.getAssetName());
        entity.setOrderSide(order.getOrderSide());
        entity.setSize(order.getSize());
        entity.setPrice(order.getPrice());
        entity.setStatus(order.getStatus());
        entity.setCreateDate(order.getCreateDate());
        return entity;
    }

    private Order toDomain(OrderEntity entity) {
        return new Order(
                entity.getId(),
                entity.getCustomer().getCustomerId(),
                entity.getAssetName(),
                entity.getOrderSide(),
                entity.getSize(),
                entity.getPrice(),
                entity.getStatus(),
                entity.getCreateDate()
        );
    }
}
