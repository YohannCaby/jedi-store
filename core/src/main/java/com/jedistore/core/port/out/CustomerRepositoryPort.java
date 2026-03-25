package com.jedistore.core.port.out;

import com.jedistore.core.domain.command.UserSearchQuery;
import com.jedistore.core.domain.entity.Customer;

import java.util.List;
import java.util.Optional;

/**
 * Port sortant (repository) pour l'accès aux données clients.
 * <p>
 * Implémenté par {@code CustomerRepositoryAdapter} dans le module {@code bdd-connector}.
 * Le domaine ne connaît pas JPA : il ne manipule que des entités domaine {@link Customer}.
 * </p>
 */
public interface CustomerRepositoryPort {

    /**
     * Recherche un client par son identifiant sans charger ses commandes.
     *
     * @param id identifiant du client
     * @return {@link Optional} contenant le client, ou vide si introuvable
     */
    Optional<Customer> findById(Long id);

    /**
     * Recherche un client avec ses commandes chargées eagerly (JOIN FETCH).
     * Utilisé pour éviter le problème N+1 lors de l'affichage du détail client.
     *
     * @param id identifiant du client
     * @return {@link Optional} contenant le client avec ses commandes, ou vide si introuvable
     */
    Optional<Customer> findByIdWithOrders(Long id);

    /**
     * Persiste un client (création ou mise à jour selon la présence d'un identifiant).
     *
     * @param customer le client à sauvegarder
     * @return le client sauvegardé avec son identifiant généré
     */
    Customer save(Customer customer);

    /**
     * Recherche des clients selon des critères dynamiques.
     *
     * @param customer objet de requête avec les critères de filtrage
     * @return liste des clients correspondants
     */
    List<Customer> search(UserSearchQuery customer);
}
