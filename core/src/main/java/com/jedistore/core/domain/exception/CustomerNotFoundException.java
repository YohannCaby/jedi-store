package com.jedistore.core.domain.exception;

import com.jedistore.core.domain.entity.Customer;

/**
 * Exception domaine levée lorsqu'un client est introuvable.
 * Traduite en HTTP 404 par le gestionnaire d'exceptions global du module {@code api}.
 */
public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(Long id) {
        super("Customer not found with id: " + id);
    }
    public CustomerNotFoundException(Customer customer){
        super("Customer not found with: "+customer.toString());
    }
}
