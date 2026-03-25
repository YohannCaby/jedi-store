package com.jedistore.core.port.in;

import com.jedistore.core.domain.command.UserSearchQuery;
import com.jedistore.core.domain.entity.Customer;
import com.jedistore.core.domain.entity.Order;

import java.util.List;

/**
 * Port entrant (use case) pour les opérations liées aux clients.
 * <p>
 * Implémenté par {@code CustomerService} dans le domaine.
 * Appelé par les adapters entrants : {@code CustomerController} (REST)
 * et les outils MCP du {@code mcp-server}.
 * </p>
 */
public interface CustomerUseCase {

    /**
     * Retourne toutes les commandes d'un client, triées par date décroissante.
     *
     * @param customerId identifiant du client
     * @return liste des commandes (vide si aucune commande)
     * @throws com.jedistore.core.domain.exception.CustomerNotFoundException si le client n'existe pas
     */
    List<Order> getOrdersByCustomerId(Long customerId);

    /**
     * Recherche des clients selon des critères optionnels (nom partiel, email exact).
     *
     * @param query critères de recherche ; les champs null ou vides sont ignorés
     * @return liste des clients correspondants (vide si aucun résultat)
     */
    List<Customer> search(UserSearchQuery query);
}
