package com.pokestore.api.controller;

import com.pokestore.api.generated.api.OrdersApi;
import com.pokestore.api.generated.model.CreateOrderRequest;
import com.pokestore.api.generated.model.OrderDto;
import com.pokestore.api.generated.model.UpdateStatusRequest;
import com.pokestore.api.mapper.DtoMapper;
import com.pokestore.core.domain.valueobject.OrderStatus;
import com.pokestore.core.port.in.OrderUseCase;
import com.pokestore.core.port.in.OrderUseCase.OrderLineRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class OrderController implements OrdersApi {

    private final OrderUseCase orderUseCase;
    private final DtoMapper dtoMapper;

    public OrderController(OrderUseCase orderUseCase, DtoMapper dtoMapper) {
        this.orderUseCase = orderUseCase;
        this.dtoMapper = dtoMapper;
    }

    @Override
    public ResponseEntity<OrderDto> createOrder(CreateOrderRequest request) {
        List<OrderLineRequest> lines = request.getLines().stream()
                .map(line -> new OrderLineRequest(line.getProductId(), line.getQuantity()))
                .toList();

        var order = orderUseCase.createOrder(request.getCustomerId(), lines);
        return ResponseEntity.status(HttpStatus.CREATED).body(dtoMapper.toOrderDto(order));
    }

    @Override
    public ResponseEntity<OrderDto> updateOrderStatus(Long id, UpdateStatusRequest request) {
        OrderStatus status = OrderStatus.valueOf(request.getStatus().getValue());
        var order = orderUseCase.updateStatus(id, status);
        return ResponseEntity.ok(dtoMapper.toOrderDto(order));
    }

    @Override
    public ResponseEntity<OrderDto> getOrderById(Long id) {
        return orderUseCase.getOrderById(id)
                .map(dtoMapper::toOrderDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
