package com.brokerage.tradeengine.domain.repository;

import com.brokerage.tradeengine.domain.model.Customer;

public interface CustomerRepository {

    Customer save(Customer customer);
}

