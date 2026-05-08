package com.brokerage.tradeengine.infrastructure.adapter.persistence.repository;

import com.brokerage.tradeengine.infrastructure.adapter.persistence.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataCustomerRepository extends JpaRepository<CustomerEntity, Long> {

    Optional<CustomerEntity> findByCustomerId(String customerId);
}

