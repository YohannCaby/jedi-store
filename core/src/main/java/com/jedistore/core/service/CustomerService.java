package com.jedistore.core.service;

import com.jedistore.core.domain.command.UserSearchQuery;
import com.jedistore.core.domain.entity.Customer;
import com.jedistore.core.domain.entity.Order;
import com.jedistore.core.domain.exception.CustomerNotFoundException;
import com.jedistore.core.port.in.CustomerUseCase;
import com.jedistore.core.port.out.CustomerRepositoryPort;
import com.jedistore.core.port.out.OrderRepositoryPort;

import java.util.List;

/**
 * Implémentation du use case {@link CustomerUseCase}.
 * <p>
 * Classe de service domaine : aucune dépendance vers Spring ou JPA.
 * Les dépendances sont injectées via le constructeur et correspondent
 * aux ports sortants du domaine.
 * </p>
 */
public class CustomerService implements CustomerUseCase {

    private final CustomerRepositoryPort customerRepository;
    private final OrderRepositoryPort orderRepository;

    public CustomerService(CustomerRepositoryPort customerRepository, OrderRepositoryPort orderRepository) {
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public List<Order> getOrdersByCustomerId(Long customerId) {
        // Vérifie l'existence du client avant de récupérer ses commandes
        customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));
        return orderRepository.findByCustomerId(customerId);
    }

    @Override
    public List<Customer> search(UserSearchQuery query) {
        return customerRepository.search(query);
    }
}
