package com.pokestore.db.adapter;

import com.pokestore.core.domain.command.UserSearchQuery;
import com.pokestore.core.domain.entity.Customer;
import com.pokestore.core.port.out.CustomerRepositoryPort;
import com.pokestore.db.entity.CustomerEntity;
import com.pokestore.db.mapper.CustomerMapper;
import com.pokestore.db.repository.CustomerJpaRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class CustomerRepositoryAdapter implements CustomerRepositoryPort {

    private final CustomerJpaRepository jpaRepository;
    private final CustomerMapper mapper;

    public CustomerRepositoryAdapter(CustomerJpaRepository jpaRepository, CustomerMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Customer> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Customer> findByIdWithOrders(Long id) {
        return jpaRepository.findByIdWithOrders(id).map(mapper::toDomain);
    }

    @Override
    public Customer save(Customer customer) {
        var entity = mapper.toEntity(customer);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public List<Customer> search(UserSearchQuery userSearchQueryuery) {
        List<Specification<CustomerEntity>> specs = new ArrayList<>();
        if (StringUtils.hasText(userSearchQueryuery.getName())){
            specs.add(((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("name"),"%"+userSearchQueryuery.getName()+"%")));
        }
        if (StringUtils.hasText(userSearchQueryuery.getEmail())){
            specs.add(((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("email"),userSearchQueryuery.getEmail())));
        }
        List<CustomerEntity> customers = jpaRepository.findAll(Specification.allOf(specs));

        return mapper.toDomain(customers);
    }
}
