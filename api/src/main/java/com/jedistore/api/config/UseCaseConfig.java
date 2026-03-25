package com.jedistore.api.config;

import com.jedistore.core.port.in.CustomerUseCase;
import com.jedistore.core.port.in.OrderUseCase;
import com.jedistore.core.port.in.ProductUseCase;
import com.jedistore.core.port.out.CustomerRepositoryPort;
import com.jedistore.core.port.out.OrderRepositoryPort;
import com.jedistore.core.port.out.ProductRepositoryPort;
import com.jedistore.core.service.CustomerService;
import com.jedistore.core.service.OrderService;
import com.jedistore.core.service.ProductService;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.jedistore.db.repository")
@EntityScan(basePackages = "com.jedistore.db.entity")
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
