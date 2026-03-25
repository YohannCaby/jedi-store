package com.jedistore.core.port.in;

import com.jedistore.core.domain.entity.Order;
import com.jedistore.core.domain.valueobject.OrderStatus;

import java.util.List;
import java.util.Optional;

/**
 * Port entrant (use case) pour les opérations liées aux commandes.
 * <p>
 * Implémenté par {@code OrderService}. Appelé par {@code OrderController}
 * et les outils MCP {@code ADMIN_createOrder} / {@code ADMIN_updateOrderStatus}.
 * </p>
 */
public interface OrderUseCase {

    /**
     * Crée une nouvelle commande pour un client.
     * <p>
     * Le prix unitaire de chaque ligne est capturé depuis le catalogue au moment
     * de la création (snapshot), garantissant l'immutabilité des prix passés.
     * </p>
     *
     * @param customerId identifiant du client commandeur
     * @param lines      liste des lignes de commande (produit + quantité)
     * @return la commande persistée avec son identifiant et son montant total calculé
     * @throws com.jedistore.core.domain.exception.CustomerNotFoundException si le client est introuvable
     * @throws com.jedistore.core.domain.exception.ProductNotFoundException  si un produit est introuvable
     */
    Order createOrder(Long customerId, List<OrderLineRequest> lines);

    /**
     * Met à jour le statut d'une commande existante.
     *
     * @param orderId   identifiant de la commande
     * @param newStatus nouveau statut à appliquer
     * @return la commande mise à jour
     * @throws com.jedistore.core.domain.exception.OrderNotFoundException si la commande est introuvable
     */
    Order updateStatus(Long orderId, OrderStatus newStatus);

    /**
     * Retourne le détail complet d'une commande avec ses lignes et produits associés.
     *
     * @param orderId identifiant de la commande
     * @return {@link Optional} contenant la commande, ou vide si introuvable
     */
    Optional<Order> getOrderById(Long orderId);

    /**
     * DTO interne représentant une ligne dans la requête de création de commande.
     *
     * @param productId identifiant du produit à commander
     * @param quantity  quantité souhaitée (doit être &gt; 0)
     */
    record OrderLineRequest(Long productId, Integer quantity) {}
}
