package com.pokestore.core.port.in;

import com.pokestore.core.domain.entity.Order;
import com.pokestore.core.domain.valueobject.OrderStatus;

import java.util.List;
import java.util.Optional;

public interface OrderUseCase {
    Order createOrder(Long customerId, List<OrderLineRequest> lines);
    Order updateStatus(Long orderId, OrderStatus newStatus);
    Optional<Order> getOrderById(Long orderId);

    record OrderLineRequest(Long productId, Integer quantity) {}
}
