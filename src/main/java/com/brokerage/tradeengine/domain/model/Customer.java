package com.brokerage.tradeengine.domain.model;

import com.brokerage.tradeengine.domain.exception.BlankFieldException;

public record Customer(String id, String name, String status) {

    public Customer {
        if (id == null || id.isBlank()) {
            throw new BlankFieldException("id");
        }
        if (name == null || name.isBlank()) {
            throw new BlankFieldException("name");
        }
    }
}

