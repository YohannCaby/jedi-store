package com.pokestore.core.port.in;

import com.pokestore.core.domain.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductUseCase {
    List<Product> getAllProducts();
    Optional<Product> getProductById(Long id);
}
