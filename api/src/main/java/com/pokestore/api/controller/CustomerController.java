package com.pokestore.api.controller;

import com.pokestore.api.controller.dto.OrderDto;
import com.pokestore.api.mapper.DtoMapper;
import com.pokestore.core.port.in.CustomerUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/customers")
@Tag(name = "Customers", description = "Gestion des clients")
public class CustomerController {

    private final CustomerUseCase customerUseCase;
    private final DtoMapper dtoMapper;

    public CustomerController(CustomerUseCase customerUseCase, DtoMapper dtoMapper) {
        this.customerUseCase = customerUseCase;
        this.dtoMapper = dtoMapper;
    }

    @GetMapping("/{id}/orders")
    @Operation(summary = "Liste les commandes d'un client", description = "Retourne toutes les commandes passees par un client")
    @ApiResponse(responseCode = "200", description = "Liste des commandes recuperee avec succes")
    public ResponseEntity<List<OrderDto>> getOrdersByCustomerId(
            @Parameter(description = "ID du client") @PathVariable Long id) {
        var orders = customerUseCase.getOrdersByCustomerId(id);
        return ResponseEntity.ok(dtoMapper.toOrderDtoList(orders));
    }
}
