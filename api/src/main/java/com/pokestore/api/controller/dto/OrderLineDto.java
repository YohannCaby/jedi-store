package com.pokestore.api.controller.dto;

import java.math.BigDecimal;

public record OrderLineDto(
        Long id,
        ProductDto product,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal
) {}
