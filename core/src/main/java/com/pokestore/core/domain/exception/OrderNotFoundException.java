package com.pokestore.core.domain.exception;

/**
 * Exception domaine levée lorsqu'une commande est introuvable.
 * Traduite en HTTP 404 par le gestionnaire d'exceptions global du module {@code api}.
 */
public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(Long id) {
        super("Order not found with id: " + id);
    }
}
