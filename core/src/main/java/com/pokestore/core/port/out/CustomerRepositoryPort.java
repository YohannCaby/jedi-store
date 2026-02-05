package com.pokestore.core.port.out;

import com.pokestore.core.domain.command.UserSearchQuery;
import com.pokestore.core.domain.entity.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerRepositoryPort {
    Optional<Customer> findById(Long id);
    Optional<Customer> findByIdWithOrders(Long id);
    Customer save(Customer customer);
    List<Customer> search(UserSearchQuery customer);
}
