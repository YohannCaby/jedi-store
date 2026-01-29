package com.pokestore.api.controller;

import com.pokestore.api.controller.dto.ProductDto;
import com.pokestore.api.mapper.DtoMapper;
import com.pokestore.core.port.in.ProductUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products")
@Tag(name = "Products", description = "Gestion des produits Pokemon")
public class ProductController {

    private final ProductUseCase productUseCase;
    private final DtoMapper dtoMapper;

    public ProductController(ProductUseCase productUseCase, DtoMapper dtoMapper) {
        this.productUseCase = productUseCase;
        this.dtoMapper = dtoMapper;
    }

    @GetMapping
    @Operation(summary = "Liste tous les produits", description = "Retourne la liste complete de tous les produits disponibles")
    @ApiResponse(responseCode = "200", description = "Liste des produits recuperee avec succes")
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        var products = productUseCase.getAllProducts();
        return ResponseEntity.ok(dtoMapper.toProductDtoList(products));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Recupere un produit par ID", description = "Retourne les details d'un produit specifique")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produit trouve"),
            @ApiResponse(responseCode = "404", description = "Produit non trouve")
    })
    public ResponseEntity<ProductDto> getProductById(
            @Parameter(description = "ID du produit") @PathVariable Long id) {
        return productUseCase.getProductById(id)
                .map(dtoMapper::toProductDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
