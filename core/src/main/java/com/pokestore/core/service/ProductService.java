package com.pokestore.core.service;

import com.pokestore.core.domain.entity.Product;
import com.pokestore.core.port.in.ProductUseCase;
import com.pokestore.core.port.out.ProductRepositoryPort;

import java.util.List;
import java.util.Optional;

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
