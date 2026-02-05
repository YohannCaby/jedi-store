package com.pokestore.core.domain.exception;

import com.pokestore.core.domain.entity.Customer;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(Long id) {
        super("Customer not found with id: " + id);
    }
    public CustomerNotFoundException(Customer customer){
        super("Customer not found with: "+customer.toString());
    }
}
