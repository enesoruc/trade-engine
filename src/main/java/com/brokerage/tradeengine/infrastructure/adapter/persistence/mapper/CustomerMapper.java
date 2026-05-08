package com.brokerage.tradeengine.infrastructure.adapter.persistence.mapper;

import com.brokerage.tradeengine.domain.model.Customer;
import com.brokerage.tradeengine.infrastructure.adapter.persistence.entity.CustomerEntity;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public Customer toDomain(CustomerEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Customer(
                entity.getCustomerId(),
                entity.getName(),
                entity.getStatus()
        );
    }

    public CustomerEntity toEntity(Customer customer) {
        if (customer == null) {
            return null;
        }
        CustomerEntity entity = new CustomerEntity();
        entity.setCustomerId(customer.id());
        entity.setName(customer.name());
        entity.setStatus(customer.status());
        return entity;
    }
}