package com.pokestore.core.service;

import com.pokestore.core.domain.command.UserSearchQuery;
import com.pokestore.core.domain.entity.Customer;
import com.pokestore.core.domain.entity.Order;
import com.pokestore.core.domain.exception.CustomerNotFoundException;
import com.pokestore.core.port.in.CustomerUseCase;
import com.pokestore.core.port.out.CustomerRepositoryPort;
import com.pokestore.core.port.out.OrderRepositoryPort;

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
