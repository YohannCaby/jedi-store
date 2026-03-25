package com.jedistore.core.domain.entity;

import com.jedistore.core.domain.valueobject.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité domaine représentant une commande client.
 * <p>
 * Une commande agrège plusieurs {@link OrderLine} et maintient son montant
 * total en cohérence via {@link #recalculateTotalAmount()}. Le statut initial
 * est {@link com.jedistore.core.domain.valueobject.OrderStatus#IN_PROGRESS}
 * et la date de création est fixée à l'instant de construction.
 * </p>
 */
public class Order {
    private Long id;
    private LocalDateTime orderDate;
    private Customer customer;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private List<OrderLine> lines = new ArrayList<>();

    /**
     * Constructeur par défaut pour la création d'une nouvelle commande.
     * Initialise la date à maintenant et le statut à {@link OrderStatus#IN_PROGRESS}.
     */
    public Order() {
        this.orderDate = LocalDateTime.now();
        this.status = OrderStatus.IN_PROGRESS;
    }

    public Order(Long id, LocalDateTime orderDate, Customer customer, OrderStatus status) {
        this.id = id;
        this.orderDate = orderDate;
        this.customer = customer;
        this.status = status;
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

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<OrderLine> getLines() {
        return lines;
    }

    public void setLines(List<OrderLine> lines) {
        this.lines = lines;
    }

    /**
     * Ajoute une ligne à la commande, maintient la relation bidirectionnelle,
     * puis recalcule le montant total.
     *
     * @param line la ligne de commande à ajouter
     */
    public void addLine(OrderLine line) {
        this.lines.add(line);
        line.setOrder(this);
        recalculateTotalAmount();
    }

    /**
     * Recalcule le montant total en sommant le sous-total de chaque ligne.
     * Doit être appelé après toute modification des lignes.
     */
    public void recalculateTotalAmount() {
        this.totalAmount = lines.stream()
                .map(OrderLine::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
