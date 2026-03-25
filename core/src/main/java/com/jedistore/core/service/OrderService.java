package com.jedistore.core.service;

import com.jedistore.core.domain.entity.Customer;
import com.jedistore.core.domain.entity.Order;
import com.jedistore.core.domain.entity.OrderLine;
import com.jedistore.core.domain.entity.Product;
import com.jedistore.core.domain.exception.CustomerNotFoundException;
import com.jedistore.core.domain.exception.OrderNotFoundException;
import com.jedistore.core.domain.exception.ProductNotFoundException;
import com.jedistore.core.domain.valueobject.OrderStatus;
import com.jedistore.core.port.in.OrderUseCase;
import com.jedistore.core.port.out.CustomerRepositoryPort;
import com.jedistore.core.port.out.OrderRepositoryPort;
import com.jedistore.core.port.out.ProductRepositoryPort;

import java.util.List;
import java.util.Optional;

/**
 * Implémentation du use case {@link OrderUseCase}.
 * <p>
 * Orchestre la création et la mise à jour des commandes en coordonnant
 * les repositories clients, produits et commandes. Classe domaine pure,
 * sans dépendance vers Spring ou JPA.
 * </p>
 */
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
            // Snapshot du prix catalogue : les modifications futures du prix
            // d'un produit n'affectent pas les commandes déjà passées.
            line.setUnitPrice(product.getPrice());
            order.addLine(line); // addLine recalcule le totalAmount automatiquement
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
