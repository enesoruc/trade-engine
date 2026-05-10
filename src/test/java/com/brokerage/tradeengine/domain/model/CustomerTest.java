package com.brokerage.tradeengine.domain.model;

import com.brokerage.tradeengine.domain.exception.BlankFieldException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CustomerTest {

    @Test
    void constructor_shouldCreateCustomerWhenFieldsAreValid() {
        Customer customer = new Customer("cust-1", "Alice", "ACTIVE");

        assertEquals("cust-1", customer.id());
        assertEquals("Alice", customer.name());
        assertEquals("ACTIVE", customer.status());
    }

    @Test
    void constructor_shouldThrowWhenIdIsBlank() {
        assertThrows(BlankFieldException.class, () -> new Customer(" ", "Alice", "ACTIVE"));
    }

    @Test
    void constructor_shouldThrowWhenNameIsBlank() {
        assertThrows(BlankFieldException.class, () -> new Customer("cust-1", " ", "ACTIVE"));
    }
}
