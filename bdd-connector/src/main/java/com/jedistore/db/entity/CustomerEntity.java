package com.jedistore.db.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité JPA mappée sur la table {@code customers}.
 * <p>
 * Classe technique propre au module {@code bdd-connector}.
 * N'est jamais exposée en dehors de ce module : la conversion vers
 * l'entité domaine {@link com.jedistore.core.domain.entity.Customer}
 * est assurée par {@code CustomerMapper}.
 * </p>
 * <p>
 * Les commandes sont chargées en {@code LAZY} pour éviter de ramener
 * l'intégralité du graph en mémoire lors des simples lookups par ID.
 * Utiliser {@code CustomerJpaRepository#findByIdWithOrders} pour un chargement eager.
 * </p>
 */
@Entity
@Table(name = "customers")
public class CustomerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    private String address;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderEntity> orders = new ArrayList<>();

    public CustomerEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<OrderEntity> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderEntity> orders) {
        this.orders = orders;
    }
}
