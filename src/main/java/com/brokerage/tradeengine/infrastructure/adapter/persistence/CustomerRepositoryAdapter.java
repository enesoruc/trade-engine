package com.brokerage.tradeengine.infrastructure.adapter.persistence;

import com.brokerage.tradeengine.domain.model.Customer;
import com.brokerage.tradeengine.domain.repository.CustomerRepository;
import com.brokerage.tradeengine.infrastructure.adapter.persistence.mapper.CustomerMapper;
import com.brokerage.tradeengine.infrastructure.adapter.persistence.entity.CustomerEntity;
import com.brokerage.tradeengine.infrastructure.adapter.persistence.repository.SpringDataCustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomerRepositoryAdapter implements CustomerRepository {

    private final SpringDataCustomerRepository springDataCustomerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public Customer save(Customer customer) {
        CustomerEntity entity = customerMapper.toEntity(customer);
        CustomerEntity savedEntity = springDataCustomerRepository.save(entity);
        return customerMapper.toDomain(savedEntity);
    }
}
