package com.jedistore.core.service;

import com.jedistore.core.domain.entity.Customer;
import com.jedistore.core.domain.entity.Order;
import com.jedistore.core.domain.exception.CustomerNotFoundException;
import com.jedistore.core.domain.valueobject.OrderStatus;
import com.jedistore.core.port.out.CustomerRepositoryPort;
import com.jedistore.core.port.out.OrderRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepositoryPort customerRepository;

    @Mock
    private OrderRepositoryPort orderRepository;

    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        customerService = new CustomerService(customerRepository, orderRepository);
    }

    @Test
    void getOrdersByCustomerId_shouldReturnOrders_whenCustomerExists() {
        Long customerId = 1L;
        Customer customer = new Customer(customerId, "Ash Ketchum", "ash@pokemon.com", "Pallet Town");
        Order order1 = new Order(1L, LocalDateTime.now(), customer, OrderStatus.IN_PROGRESS);
        Order order2 = new Order(2L, LocalDateTime.now(), customer, OrderStatus.DELIVERED);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(orderRepository.findByCustomerId(customerId)).thenReturn(List.of(order1, order2));

        List<Order> result = customerService.getOrdersByCustomerId(customerId);

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(order1, order2);
    }

    @Test
    void getOrdersByCustomerId_shouldThrowException_whenCustomerNotFound() {
        Long customerId = 999L;
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.getOrdersByCustomerId(customerId))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void getOrdersByCustomerId_shouldReturnEmptyList_whenCustomerHasNoOrders() {
        Long customerId = 1L;
        Customer customer = new Customer(customerId, "Ash Ketchum", "ash@pokemon.com", "Pallet Town");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(orderRepository.findByCustomerId(customerId)).thenReturn(List.of());

        List<Order> result = customerService.getOrdersByCustomerId(customerId);

        assertThat(result).isEmpty();
    }
}
