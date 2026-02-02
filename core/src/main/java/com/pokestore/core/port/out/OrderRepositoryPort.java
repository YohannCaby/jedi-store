package com.pokestore.core.port.out;

import com.pokestore.core.domain.entity.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepositoryPort {
    Optional<Order> findById(Long id);
    Optional<Order> findByIdWithLines(Long id);
    List<Order> findByCustomerId(Long customerId);
    Order save(Order order);
}
