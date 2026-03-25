package com.jedistore.core.service;

import com.jedistore.core.domain.entity.Product;
import com.jedistore.core.port.in.ProductUseCase;
import com.jedistore.core.port.out.ProductRepositoryPort;

import java.util.List;
import java.util.Optional;

/**
 * Implémentation du use case {@link ProductUseCase}.
 * Classe domaine pure sans dépendance vers Spring ou JPA.
 */
public class ProductService implements ProductUseCase {

    private final ProductRepositoryPort productRepository;

    public ProductService(ProductRepositoryPort productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
}
