package com.pokestore.api.config;

import com.pokestore.core.port.in.CustomerUseCase;
import com.pokestore.core.port.in.OrderUseCase;
import com.pokestore.core.port.in.ProductUseCase;
import com.pokestore.core.port.out.CustomerRepositoryPort;
import com.pokestore.core.port.out.OrderRepositoryPort;
import com.pokestore.core.port.out.ProductRepositoryPort;
import com.pokestore.core.service.CustomerService;
import com.pokestore.core.service.OrderService;
import com.pokestore.core.service.ProductService;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.pokestore.db.repository")
@EntityScan(basePackages = "com.pokestore.db.entity")
@EnableTransactionManagement
public class UseCaseConfig {

    @Bean
    public CustomerUseCase customerUseCase(CustomerRepositoryPort customerRepository,
                                           OrderRepositoryPort orderRepository) {
        return new CustomerService(customerRepository, orderRepository);
    }

    @Bean
    public ProductUseCase productUseCase(ProductRepositoryPort productRepository) {
        return new ProductService(productRepository);
    }

    @Bean
    public OrderUseCase orderUseCase(OrderRepositoryPort orderRepository,
                                     CustomerRepositoryPort customerRepository,
                                     ProductRepositoryPort productRepository) {
        return new OrderService(orderRepository, customerRepository, productRepository);
    }
}
