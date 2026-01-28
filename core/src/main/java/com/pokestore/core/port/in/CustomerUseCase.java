package com.pokestore.core.port.in;

import com.pokestore.core.domain.entity.Order;

import java.util.List;

public interface CustomerUseCase {
    List<Order> getOrdersByCustomerId(Long customerId);
}
