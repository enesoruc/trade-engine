package com.brokerage.tradeengine.infrastructure.adapter.persistence.repository;

import com.brokerage.tradeengine.infrastructure.adapter.persistence.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SpringDataCustomerRepository extends JpaRepository<CustomerEntity, Long> {
    List<CustomerEntity> findByCustomerIdIn(Collection<String> customerIds);
}

