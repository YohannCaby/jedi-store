package com.pokestore.api.controller;

import com.pokestore.api.controller.dto.CreateOrderRequest;
import com.pokestore.api.controller.dto.OrderDto;
import com.pokestore.api.controller.dto.UpdateStatusRequest;
import com.pokestore.api.mapper.DtoMapper;
import com.pokestore.core.domain.valueobject.OrderStatus;
import com.pokestore.core.port.in.OrderUseCase;
import com.pokestore.core.port.in.OrderUseCase.OrderLineRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderUseCase orderUseCase;
    private final DtoMapper dtoMapper;

    public OrderController(OrderUseCase orderUseCase, DtoMapper dtoMapper) {
        this.orderUseCase = orderUseCase;
        this.dtoMapper = dtoMapper;
    }

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        List<OrderLineRequest> lines = request.lines().stream()
                .map(line -> new OrderLineRequest(line.productId(), line.quantity()))
                .toList();

        var order = orderUseCase.createOrder(request.customerId(), lines);
        return ResponseEntity.status(HttpStatus.CREATED).body(dtoMapper.toOrderDto(order));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderDto> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatusRequest request) {
        OrderStatus status = OrderStatus.valueOf(request.status());
        var order = orderUseCase.updateStatus(id, status);
        return ResponseEntity.ok(dtoMapper.toOrderDto(order));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable Long id) {
        return orderUseCase.getOrderById(id)
                .map(dtoMapper::toOrderDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
