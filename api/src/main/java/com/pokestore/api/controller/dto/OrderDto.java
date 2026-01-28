package com.pokestore.api.controller.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDto(
        Long id,
        LocalDateTime orderDate,
        CustomerDto customer,
        String status,
        BigDecimal totalAmount,
        List<OrderLineDto> lines
) {}
