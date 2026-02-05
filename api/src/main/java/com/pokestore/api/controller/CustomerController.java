package com.pokestore.api.controller;

import com.pokestore.api.generated.api.CustomersApi;
import com.pokestore.api.generated.model.CustomersDto;
import com.pokestore.api.generated.model.OrderDto;
import com.pokestore.api.generated.model.UserSearchQueryDto;
import com.pokestore.api.mapper.DtoMapper;
import com.pokestore.core.domain.command.UserSearchQuery;
import com.pokestore.core.port.in.CustomerUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CustomerController implements CustomersApi {

    private final CustomerUseCase customerUseCase;
    private final DtoMapper dtoMapper;

    public CustomerController(CustomerUseCase customerUseCase, DtoMapper dtoMapper) {
        this.customerUseCase = customerUseCase;
        this.dtoMapper = dtoMapper;
    }

    @Override
    public ResponseEntity<List<OrderDto>> getOrdersByCustomerId(Long id) {
        var orders = customerUseCase.getOrdersByCustomerId(id);
        return ResponseEntity.ok(dtoMapper.toOrderDtoList(orders));
    }

    @Override
    public ResponseEntity<CustomersDto> searchCustomers(String name, String email) {
        UserSearchQuery query = dtoMapper.toUserSearchQuery(name,email);
        return ResponseEntity.ok(dtoMapper.toCustomersDto(customerUseCase.search(query)));
    }

}
