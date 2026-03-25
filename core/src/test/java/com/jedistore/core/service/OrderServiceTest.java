package com.jedistore.core.service;

import com.jedistore.core.domain.entity.Customer;
import com.jedistore.core.domain.entity.Order;
import com.jedistore.core.domain.entity.Product;
import com.jedistore.core.domain.exception.CustomerNotFoundException;
import com.jedistore.core.domain.exception.OrderNotFoundException;
import com.jedistore.core.domain.exception.ProductNotFoundException;
import com.jedistore.core.domain.valueobject.OrderStatus;
import com.jedistore.core.port.in.OrderUseCase.OrderLineRequest;
import com.jedistore.core.port.out.CustomerRepositoryPort;
import com.jedistore.core.port.out.OrderRepositoryPort;
import com.jedistore.core.port.out.ProductRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepositoryPort orderRepository;

    @Mock
    private CustomerRepositoryPort customerRepository;

    @Mock
    private ProductRepositoryPort productRepository;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository, customerRepository, productRepository);
    }

    @Test
    void createOrder_shouldCreateOrderWithLines() {
        Long customerId = 1L;
        Customer customer = new Customer(customerId, "Ash Ketchum", "ash@pokemon.com", "Pallet Town");
        Product pikachu = new Product(1L, "Pikachu Plush", "Cute plush", new BigDecimal("29.99"), "Plush");
        Product pokeball = new Product(2L, "Pokeball", "Standard ball", new BigDecimal("9.99"), "Equipment");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(pikachu));
        when(productRepository.findById(2L)).thenReturn(Optional.of(pokeball));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            return order;
        });

        List<OrderLineRequest> lines = List.of(
                new OrderLineRequest(1L, 2),
                new OrderLineRequest(2L, 5)
        );

        Order result = orderService.createOrder(customerId, lines);

        assertThat(result.getCustomer()).isEqualTo(customer);
        assertThat(result.getStatus()).isEqualTo(OrderStatus.IN_PROGRESS);
        assertThat(result.getLines()).hasSize(2);

        BigDecimal expectedTotal = new BigDecimal("29.99").multiply(BigDecimal.valueOf(2))
                .add(new BigDecimal("9.99").multiply(BigDecimal.valueOf(5)));
        assertThat(result.getTotalAmount()).isEqualByComparingTo(expectedTotal);
    }

    @Test
    void createOrder_shouldThrowException_whenCustomerNotFound() {
        Long customerId = 999L;
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        List<OrderLineRequest> lines = List.of(new OrderLineRequest(1L, 1));

        assertThatThrownBy(() -> orderService.createOrder(customerId, lines))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void createOrder_shouldThrowException_whenProductNotFound() {
        Long customerId = 1L;
        Customer customer = new Customer(customerId, "Ash Ketchum", "ash@pokemon.com", "Pallet Town");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        List<OrderLineRequest> lines = List.of(new OrderLineRequest(999L, 1));

        assertThatThrownBy(() -> orderService.createOrder(customerId, lines))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void updateStatus_shouldUpdateOrderStatus() {
        Long orderId = 1L;
        Customer customer = new Customer(1L, "Ash Ketchum", "ash@pokemon.com", "Pallet Town");
        Order existingOrder = new Order(orderId, LocalDateTime.now(), customer, OrderStatus.IN_PROGRESS);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order result = orderService.updateStatus(orderId, OrderStatus.DELIVERED);

        assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERED);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        assertThat(orderCaptor.getValue().getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @Test
    void updateStatus_shouldThrowException_whenOrderNotFound() {
        Long orderId = 999L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.updateStatus(orderId, OrderStatus.DELIVERED))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void getOrderById_shouldReturnOrder_whenExists() {
        Long orderId = 1L;
        Customer customer = new Customer(1L, "Ash Ketchum", "ash@pokemon.com", "Pallet Town");
        Order order = new Order(orderId, LocalDateTime.now(), customer, OrderStatus.IN_PROGRESS);

        when(orderRepository.findByIdWithLines(orderId)).thenReturn(Optional.of(order));

        Optional<Order> result = orderService.getOrderById(orderId);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(orderId);
    }

    @Test
    void getOrderById_shouldReturnEmpty_whenNotExists() {
        Long orderId = 999L;
        when(orderRepository.findByIdWithLines(orderId)).thenReturn(Optional.empty());

        Optional<Order> result = orderService.getOrderById(orderId);

        assertThat(result).isEmpty();
    }
}
