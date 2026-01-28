package com.pokestore.core.service;

import com.pokestore.core.domain.entity.Product;
import com.pokestore.core.port.out.ProductRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepositoryPort productRepository;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository);
    }

    @Test
    void getAllProducts_shouldReturnAllProducts() {
        Product pikachu = new Product(1L, "Pikachu Plush", "Cute Pikachu plush toy", new BigDecimal("29.99"), "Plush");
        Product pokeball = new Product(2L, "Pokeball", "Standard Pokeball", new BigDecimal("9.99"), "Equipment");

        when(productRepository.findAll()).thenReturn(List.of(pikachu, pokeball));

        List<Product> result = productService.getAllProducts();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Product::getName).containsExactly("Pikachu Plush", "Pokeball");
    }

    @Test
    void getAllProducts_shouldReturnEmptyList_whenNoProducts() {
        when(productRepository.findAll()).thenReturn(List.of());

        List<Product> result = productService.getAllProducts();

        assertThat(result).isEmpty();
    }

    @Test
    void getProductById_shouldReturnProduct_whenExists() {
        Long productId = 1L;
        Product pikachu = new Product(productId, "Pikachu Plush", "Cute Pikachu plush toy", new BigDecimal("29.99"), "Plush");

        when(productRepository.findById(productId)).thenReturn(Optional.of(pikachu));

        Optional<Product> result = productService.getProductById(productId);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Pikachu Plush");
    }

    @Test
    void getProductById_shouldReturnEmpty_whenNotExists() {
        Long productId = 999L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        Optional<Product> result = productService.getProductById(productId);

        assertThat(result).isEmpty();
    }
}
