package com.pokestore.api.controller;

import com.pokestore.api.controller.dto.ProductDto;
import com.pokestore.api.mapper.DtoMapper;
import com.pokestore.core.port.in.ProductUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductUseCase productUseCase;
    private final DtoMapper dtoMapper;

    public ProductController(ProductUseCase productUseCase, DtoMapper dtoMapper) {
        this.productUseCase = productUseCase;
        this.dtoMapper = dtoMapper;
    }

    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        var products = productUseCase.getAllProducts();
        return ResponseEntity.ok(dtoMapper.toProductDtoList(products));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        return productUseCase.getProductById(id)
                .map(dtoMapper::toProductDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
