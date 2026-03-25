package com.jedistore.db.mapper;

import com.jedistore.core.domain.entity.Customer;
import com.jedistore.db.entity.CustomerEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapper responsable de la conversion entre l'entité domaine {@link com.jedistore.core.domain.entity.Customer}
 * et l'entité JPA {@link com.jedistore.db.entity.CustomerEntity}.
 * <p>
 * Note : la conversion {@code toDomain} ne charge pas les commandes du client
 * (la liste reste vide). Pour charger les commandes, utiliser
 * {@code CustomerJpaRepository#findByIdWithOrders}.
 * </p>
 */
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
