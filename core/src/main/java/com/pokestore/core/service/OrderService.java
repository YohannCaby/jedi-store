package com.pokestore.core.service;

import com.pokestore.core.domain.entity.Customer;
import com.pokestore.core.domain.entity.Order;
import com.pokestore.core.domain.entity.OrderLine;
import com.pokestore.core.domain.entity.Product;
import com.pokestore.core.domain.exception.CustomerNotFoundException;
import com.pokestore.core.domain.exception.OrderNotFoundException;
import com.pokestore.core.domain.exception.ProductNotFoundException;
import com.pokestore.core.domain.valueobject.OrderStatus;
import com.pokestore.core.port.in.OrderUseCase;
import com.pokestore.core.port.out.CustomerRepositoryPort;
import com.pokestore.core.port.out.OrderRepositoryPort;
import com.pokestore.core.port.out.ProductRepositoryPort;

import java.util.List;
import java.util.Optional;

public class OrderService implements OrderUseCase {

    private final OrderRepositoryPort orderRepository;
    private final CustomerRepositoryPort customerRepository;
    private final ProductRepositoryPort productRepository;

    public OrderService(OrderRepositoryPort orderRepository,
                        CustomerRepositoryPort customerRepository,
                        ProductRepositoryPort productRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    @Override
    public Order createOrder(Long customerId, List<OrderLineRequest> lineRequests) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        Order order = new Order();
        order.setCustomer(customer);

        for (OrderLineRequest request : lineRequests) {
            Product product = productRepository.findById(request.productId())
                    .orElseThrow(() -> new ProductNotFoundException(request.productId()));

            OrderLine line = new OrderLine();
            line.setProduct(product);
            line.setQuantity(request.quantity());
            line.setUnitPrice(product.getPrice());
            order.addLine(line);
        }

        return orderRepository.save(order);
    }

    @Override
    public Order updateStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    @Override
    public Optional<Order> getOrderById(Long orderId) {
        return orderRepository.findByIdWithLines(orderId);
    }
}
