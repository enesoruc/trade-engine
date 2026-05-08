package com.brokerage.tradeengine.infrastructure.adapter.persistence.repository;

import com.brokerage.tradeengine.infrastructure.adapter.persistence.entity.OrderEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SpringDataOrderRepository extends JpaRepository<OrderEntity, Long>, JpaSpecificationExecutor<OrderEntity> {

    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    java.util.Optional<OrderEntity> findById(Long id);
}
