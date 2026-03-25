package com.jedistore.core.port.in;

import com.jedistore.core.domain.entity.Product;

import java.util.List;
import java.util.Optional;

/**
 * Port entrant (use case) pour les opérations liées aux produits.
 * <p>
 * Implémenté par {@code ProductService}. Appelé par {@code ProductController}
 * et les outils MCP du {@code mcp-server} (notamment {@code USER_searchProducts}).
 * </p>
 */
public interface ProductUseCase {

    /**
     * Retourne le catalogue complet des produits disponibles.
     *
     * @return liste de tous les produits (jamais null)
     */
    List<Product> getAllProducts();

    /**
     * Recherche un produit par son identifiant.
     *
     * @param id identifiant du produit
     * @return {@link Optional} contenant le produit, ou vide si introuvable
     */
    Optional<Product> getProductById(Long id);
}
