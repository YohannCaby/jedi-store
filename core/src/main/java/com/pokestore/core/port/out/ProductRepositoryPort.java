package com.pokestore.core.port.out;

import com.pokestore.core.domain.entity.Product;

import java.util.List;
import java.util.Optional;

/**
 * Port sortant (repository) pour l'accès aux données produits.
 * Implémenté par {@code ProductRepositoryAdapter} dans le module {@code bdd-connector}.
 */
public interface ProductRepositoryPort {

    /**
     * Retourne l'intégralité du catalogue produits.
     *
     * @return liste de tous les produits (jamais null)
     */
    List<Product> findAll();

    /**
     * Recherche un produit par son identifiant.
     *
     * @param id identifiant du produit
     * @return {@link Optional} contenant le produit, ou vide si introuvable
     */
    Optional<Product> findById(Long id);

    /**
     * Persiste un produit (création ou mise à jour).
     *
     * @param product le produit à sauvegarder
     * @return le produit sauvegardé avec son identifiant généré
     */
    Product save(Product product);
}
