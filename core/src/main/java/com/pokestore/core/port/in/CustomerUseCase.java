package com.pokestore.core.port.in;

import com.pokestore.core.domain.command.UserSearchQuery;
import com.pokestore.core.domain.entity.Customer;
import com.pokestore.core.domain.entity.Order;

import java.util.List;

public interface CustomerUseCase {
    List<Order> getOrdersByCustomerId(Long customerId);
    List<Customer> search(UserSearchQuery query);
}
