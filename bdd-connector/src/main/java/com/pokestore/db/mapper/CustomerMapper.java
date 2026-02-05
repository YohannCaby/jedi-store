package com.pokestore.db.mapper;

import com.pokestore.core.domain.entity.Customer;
import com.pokestore.db.entity.CustomerEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CustomerMapper {

    public List<Customer> toDomain(List<CustomerEntity> entities) {
        List<Customer> customers = new ArrayList<>();
        if (entities == null) {
            return customers;
        }
        entities.forEach((entity) -> customers.add(toDomain(entity)));
        return customers;
    }

    public Customer toDomain(CustomerEntity entity) {
        if (entity == null) {
            return null;
        }
        Customer customer = new Customer();
        customer.setId(entity.getId());
        customer.setName(entity.getName());
        customer.setEmail(entity.getEmail());
        customer.setAddress(entity.getAddress());
        return customer;
    }

    public CustomerEntity toEntity(Customer domain) {
        if (domain == null) {
            return null;
        }
        CustomerEntity entity = new CustomerEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setEmail(domain.getEmail());
        entity.setAddress(domain.getAddress());
        return entity;
    }
}
