package com.jedistore.db.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité JPA mappée sur la table {@code orders}.
 * <p>
 * Les lignes de commande ({@code OrderLineEntity}) sont liées par
 * {@code CascadeType.ALL} + {@code orphanRemoval = true} : toute modification
 * de la collection {@code lines} est automatiquement propagée en base.
 * </p>
 * <p>
 * Le statut est stocké en chaîne ({@code EnumType.STRING}) via {@code OrderStatusEntity}
 * pour garantir la lisibilité des données en base et la robustesse aux réorganisations
 * de l'enum.
 * </p>
 */
@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerEntity customer;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatusEntity status;

    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderLineEntity> lines = new ArrayList<>();

    public OrderEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }

    public OrderStatusEntity getStatus() {
        return status;
    }

    public void setStatus(OrderStatusEntity status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<OrderLineEntity> getLines() {
        return lines;
    }

    public void setLines(List<OrderLineEntity> lines) {
        this.lines = lines;
    }

    /**
     * Ajoute une ligne à la commande et maintient la relation bidirectionnelle.
     * La ligne reçoit une référence vers cette commande (nécessaire pour la FK en base).
     *
     * @param line la ligne de commande à associer
     */
    public void addLine(OrderLineEntity line) {
        lines.add(line);
        line.setOrder(this);
    }
}
