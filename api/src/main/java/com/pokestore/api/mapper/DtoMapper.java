package com.pokestore.api.mapper;

import com.pokestore.api.controller.dto.*;
import com.pokestore.core.domain.entity.Customer;
import com.pokestore.core.domain.entity.Order;
import com.pokestore.core.domain.entity.OrderLine;
import com.pokestore.core.domain.entity.Product;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class DtoMapper {

    public CustomerDto toCustomerDto(Customer customer) {
        if (customer == null) {
            return null;
        }
        return new CustomerDto(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getAddress()
        );
    }

    public ProductDto toProductDto(Product product) {
        if (product == null) {
            return null;
        }
        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCategory()
        );
    }

    public OrderLineDto toOrderLineDto(OrderLine line) {
        if (line == null) {
            return null;
        }
        return new OrderLineDto(
                line.getId(),
                toProductDto(line.getProduct()),
                line.getQuantity(),
                line.getUnitPrice(),
                line.getLineTotal()
        );
    }

    public OrderDto toOrderDto(Order order) {
        if (order == null) {
            return null;
        }
        List<OrderLineDto> lineDtos = order.getLines() != null
                ? order.getLines().stream().map(this::toOrderLineDto).toList()
                : Collections.emptyList();

        return new OrderDto(
                order.getId(),
                order.getOrderDate(),
                toCustomerDto(order.getCustomer()),
                order.getStatus() != null ? order.getStatus().name() : null,
                order.getTotalAmount(),
                lineDtos
        );
    }

    public List<OrderDto> toOrderDtoList(List<Order> orders) {
        if (orders == null) {
            return Collections.emptyList();
        }
        return orders.stream().map(this::toOrderDto).toList();
    }

    public List<ProductDto> toProductDtoList(List<Product> products) {
        if (products == null) {
            return Collections.emptyList();
        }
        return products.stream().map(this::toProductDto).toList();
    }
}
