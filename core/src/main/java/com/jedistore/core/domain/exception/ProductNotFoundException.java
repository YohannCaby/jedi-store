package com.jedistore.core.domain.exception;

/**
 * Exception domaine levée lorsqu'un produit est introuvable.
 * Traduite en HTTP 404 par le gestionnaire d'exceptions global du module {@code api}.
 */
public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Long id) {
        super("Product not found with id: " + id);
    }
}
