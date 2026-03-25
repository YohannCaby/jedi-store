package com.jedistore.api.mapper;

import com.jedistore.api.generated.model.*;
import com.jedistore.core.domain.command.UserSearchQuery;
import com.jedistore.core.domain.entity.Customer;
import com.jedistore.core.domain.entity.Order;
import com.jedistore.core.domain.entity.OrderLine;
import com.jedistore.core.domain.entity.Product;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;

@Component
public class DtoMapper {

    public CustomersDto toCustomersDto(List<Customer> customers) {
        CustomersDto customersDto = new CustomersDto();
        if (customers == null) {
            return customersDto;
        }
        return new CustomersDto().data(customers.stream().map(this::toCustomerDto).toList());
    }

    public CustomerDto toCustomerDto(Customer customer) {
        if (customer == null) {
            return null;
        }
        CustomerDto dto = new CustomerDto();
        dto.setId(customer.getId());
        dto.setName(customer.getName());
        dto.setEmail(customer.getEmail());
        dto.setAddress(customer.getAddress());
        return dto;
    }

    public ProductDto toProductDto(Product product) {
        if (product == null) {
            return null;
        }
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setCategory(product.getCategory());
        return dto;
    }

    public OrderLineDto toOrderLineDto(OrderLine line) {
        if (line == null) {
            return null;
        }
        OrderLineDto dto = new OrderLineDto();
        dto.setId(line.getId());
        dto.setProduct(toProductDto(line.getProduct()));
        dto.setQuantity(line.getQuantity());
        dto.setUnitPrice(line.getUnitPrice());
        dto.setLineTotal(line.getLineTotal());
        return dto;
    }

    public OrderDto toOrderDto(Order order) {
        if (order == null) {
            return null;
        }
        List<OrderLineDto> lineDtos = order.getLines() != null
                ? order.getLines().stream().map(this::toOrderLineDto).toList()
                : Collections.emptyList();

        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setOrderDate(order.getOrderDate() != null
                ? order.getOrderDate().atOffset(ZoneOffset.UTC)
                : null);
        dto.setCustomer(toCustomerDto(order.getCustomer()));
        dto.setStatus(order.getStatus() != null
                ? OrderDto.StatusEnum.fromValue(order.getStatus().name())
                : null);
        dto.setTotalAmount(order.getTotalAmount());
        dto.setLines(lineDtos);
        return dto;
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

    public UserSearchQuery toUserSearchQuery(String name, String email){
        UserSearchQuery query = new UserSearchQuery();
        query.setEmail(email);
        query.setName(name);
        return query;
    }
}
