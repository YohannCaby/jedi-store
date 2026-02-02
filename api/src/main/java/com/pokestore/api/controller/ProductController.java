package com.pokestore.api.controller;

import com.pokestore.api.generated.api.ProductsApi;
import com.pokestore.api.generated.model.ProductDto;
import com.pokestore.api.mapper.DtoMapper;
import com.pokestore.core.port.in.ProductUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProductController implements ProductsApi {

    private final ProductUseCase productUseCase;
    private final DtoMapper dtoMapper;

    public ProductController(ProductUseCase productUseCase, DtoMapper dtoMapper) {
        this.productUseCase = productUseCase;
        this.dtoMapper = dtoMapper;
    }

    @Override
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        var products = productUseCase.getAllProducts();
        return ResponseEntity.ok(dtoMapper.toProductDtoList(products));
    }

    @Override
    public ResponseEntity<ProductDto> getProductById(Long id) {
        return productUseCase.getProductById(id)
                .map(dtoMapper::toProductDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
