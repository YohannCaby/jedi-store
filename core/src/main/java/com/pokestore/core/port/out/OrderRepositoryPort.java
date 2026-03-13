package com.pokestore.core.port.out;

import com.pokestore.core.domain.entity.Order;

import java.util.List;
import java.util.Optional;

/**
 * Port sortant (repository) pour l'accès aux données commandes.
 * Implémenté par {@code OrderRepositoryAdapter} dans le module {@code bdd-connector}.
 */
public interface OrderRepositoryPort {

    /**
     * Recherche une commande par son identifiant sans charger les lignes.
     * Utilisé pour les opérations de mise à jour de statut.
     *
     * @param id identifiant de la commande
     * @return {@link Optional} contenant la commande, ou vide si introuvable
     */
    Optional<Order> findById(Long id);

    /**
     * Recherche une commande avec ses lignes et produits chargés eagerly (JOIN FETCH).
     * Utilisé pour le détail complet d'une commande, évite les requêtes N+1.
     *
     * @param id identifiant de la commande
     * @return {@link Optional} contenant la commande avec ses lignes, ou vide si introuvable
     */
    Optional<Order> findByIdWithLines(Long id);

    /**
     * Retourne toutes les commandes d'un client, triées par date décroissante.
     *
     * @param customerId identifiant du client
     * @return liste des commandes (vide si aucune)
     */
    List<Order> findByCustomerId(Long customerId);

    /**
     * Persiste une commande avec ses lignes.
     * Gère la création et la mise à jour (upsert basé sur la présence de l'identifiant).
     *
     * @param order la commande à sauvegarder
     * @return la commande sauvegardée avec son identifiant généré
     */
    Order save(Order order);
}
