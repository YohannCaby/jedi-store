package com.pokestore.db.repository;

import com.pokestore.db.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository JPA pour les commandes.
 * Les requêtes custom utilisent JOIN FETCH pour éviter le problème N+1
 * lors du chargement des collections LAZY (lignes et produits).
 */
@Repository
public interface OrderJpaRepository extends JpaRepository<OrderEntity, Long> {

    /**
     * Charge une commande avec ses lignes et les produits associés en une seule requête.
     * Utilisé par {@code OrderUseCase#getOrderById} pour le détail complet.
     *
     * @param id identifiant de la commande
     * @return la commande avec {@code lines} et {@code lines.product} initialisés
     */
    @Query("SELECT o FROM OrderEntity o LEFT JOIN FETCH o.lines l LEFT JOIN FETCH l.product WHERE o.id = :id")
    Optional<OrderEntity> findByIdWithLines(@Param("id") Long id);

    /**
     * Retourne toutes les commandes d'un client avec leurs lignes, triées par date décroissante.
     * {@code DISTINCT} est nécessaire car le JOIN FETCH sur une collection peut générer
     * des doublons au niveau SQL.
     *
     * @param customerId identifiant du client
     * @return liste des commandes avec leurs lignes initialisées
     */
    @Query("SELECT DISTINCT o FROM OrderEntity o LEFT JOIN FETCH o.lines WHERE o.customer.id = :customerId ORDER BY o.orderDate DESC")
    List<OrderEntity> findByCustomerId(@Param("customerId") Long customerId);
}
