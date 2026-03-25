package com.jedistore.core.domain.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Entité domaine représentant un client du magasin Pokémon.
 * <p>
 * Cette classe est un POJO pur du domaine métier : elle ne contient aucune
 * annotation de persistance (JPA) ni de framework. La persistance est gérée
 * par le module {@code bdd-connector} via l'adapter {@code CustomerRepositoryAdapter}.
 * </p>
 */
public class Customer {
    private Long id;
    private String name;
    private String email;
    private String address;
    private List<Order> orders = new ArrayList<>();

    public Customer() {
    }

    /**
     * Constructeur principal utilisé lors de la reconstruction depuis la persistance.
     *
     * @param id      identifiant unique du client
     * @param name    nom complet
     * @param email   adresse email (unique dans le système)
     * @param address adresse postale de livraison
     */
    public Customer(Long id, String name, String email, String address) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.address = address;
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

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    /**
     * Ajoute une commande à ce client et maintient la relation bidirectionnelle.
     * La commande reçoit une référence vers ce client.
     *
     * @param order la commande à associer
     */
    public void addOrder(Order order) {
        this.orders.add(order);
        order.setCustomer(this);
    }
}
