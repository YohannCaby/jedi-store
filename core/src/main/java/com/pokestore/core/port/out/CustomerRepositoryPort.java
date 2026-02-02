package com.pokestore.core.port.out;

import com.pokestore.core.domain.entity.Customer;

import java.util.Optional;

public interface CustomerRepositoryPort {
    Optional<Customer> findById(Long id);
    Optional<Customer> findByIdWithOrders(Long id);
    Customer save(Customer customer);
}
