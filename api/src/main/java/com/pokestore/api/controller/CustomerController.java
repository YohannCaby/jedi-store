package com.pokestore.api.controller;

import com.pokestore.api.controller.dto.OrderDto;
import com.pokestore.api.mapper.DtoMapper;
import com.pokestore.core.port.in.CustomerUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerUseCase customerUseCase;
    private final DtoMapper dtoMapper;

    public CustomerController(CustomerUseCase customerUseCase, DtoMapper dtoMapper) {
        this.customerUseCase = customerUseCase;
        this.dtoMapper = dtoMapper;
    }

    @GetMapping("/{id}/orders")
    public ResponseEntity<List<OrderDto>> getOrdersByCustomerId(@PathVariable Long id) {
        var orders = customerUseCase.getOrdersByCustomerId(id);
        return ResponseEntity.ok(dtoMapper.toOrderDtoList(orders));
    }
}
