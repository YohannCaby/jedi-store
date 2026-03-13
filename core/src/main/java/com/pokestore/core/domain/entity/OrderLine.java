package com.pokestore.core.domain.entity;

import java.math.BigDecimal;

/**
 * Entité domaine représentant une ligne d'une commande.
 * <p>
 * Chaque ligne est associée à un {@link Product} avec une quantité et un prix
 * unitaire. Le prix unitaire est capturé au moment de la création de la commande
 * (snapshot du prix catalogue), de sorte qu'une modification ultérieure du prix
 * d'un produit n'affecte pas les commandes passées.
 * </p>
 */
public class OrderLine {
    private Long id;
    private Order order;
    private Product product;
    private Integer quantity;
    private BigDecimal unitPrice;

    public OrderLine() {
    }

    public OrderLine(Long id, Product product, Integer quantity, BigDecimal unitPrice) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    /**
     * Calcule le sous-total de cette ligne ({@code unitPrice × quantity}).
     * Retourne {@link BigDecimal#ZERO} si le prix ou la quantité est nul.
     *
     * @return le montant total de la ligne
     */
    public BigDecimal getLineTotal() {
        if (unitPrice == null || quantity == null) {
            return BigDecimal.ZERO;
        }
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
