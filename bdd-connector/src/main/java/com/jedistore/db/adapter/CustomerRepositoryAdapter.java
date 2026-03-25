package com.jedistore.db.adapter;

import com.jedistore.core.domain.command.UserSearchQuery;
import com.jedistore.core.domain.entity.Customer;
import com.jedistore.core.port.out.CustomerRepositoryPort;
import com.jedistore.db.entity.CustomerEntity;
import com.jedistore.db.mapper.CustomerMapper;
import com.jedistore.db.repository.CustomerJpaRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Adapter sortant implémentant le port {@link CustomerRepositoryPort}.
 * <p>
 * Fait le pont entre le domaine et la couche JPA : convertit les entités domaine
 * en entités JPA (et inversement) via {@link CustomerMapper}, et délègue
 * les opérations à {@link CustomerJpaRepository}.
 * </p>
 * <p>
 * La méthode {@code search} construit des {@code Specification} JPA dynamiquement :
 * seuls les critères renseignés dans le {@code UserSearchQuery} sont ajoutés
 * à la clause WHERE.
 * </p>
 */
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

        // Filtre sur le nom : correspondance partielle insensible à la casse (LIKE %name%)
        if (StringUtils.hasText(userSearchQueryuery.getName())){
            specs.add(((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("name"),"%"+userSearchQueryuery.getName()+"%")));
        }

        // Filtre sur l'email : correspondance exacte (= email)
        if (StringUtils.hasText(userSearchQueryuery.getEmail())){
            specs.add(((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("email"),userSearchQueryuery.getEmail())));
        }

        // Specification.allOf retourne une Specification neutre (sans WHERE) si la liste est vide
        List<CustomerEntity> customers = jpaRepository.findAll(Specification.allOf(specs));

        return mapper.toDomain(customers);
    }
}
