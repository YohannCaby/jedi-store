package com.jedistore.core.domain.command;

/**
 * Objet de requête pour la recherche de clients.
 * <p>
 * Les champs sont optionnels : seuls les champs renseignés sont appliqués
 * comme critères de filtrage. La logique de recherche est implémentée dans
 * {@code CustomerRepositoryAdapter} via JPA Specification.
 * <ul>
 *   <li>{@code name} : filtre par correspondance partielle (LIKE %name%)</li>
 *   <li>{@code email} : filtre par correspondance exacte (= email)</li>
 * </ul>
 * </p>
 */
public class UserSearchQuery {
    String name;
    String email;

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
}
