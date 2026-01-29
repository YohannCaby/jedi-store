package com.pokestore.api.controller;

import com.pokestore.api.controller.dto.CreateOrderRequest;
import com.pokestore.api.controller.dto.OrderDto;
import com.pokestore.api.controller.dto.UpdateStatusRequest;
import com.pokestore.api.mapper.DtoMapper;
import com.pokestore.core.domain.valueobject.OrderStatus;
import com.pokestore.core.port.in.OrderUseCase;
import com.pokestore.core.port.in.OrderUseCase.OrderLineRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@Tag(name = "Orders", description = "Gestion des commandes")
public class OrderController {

    private final OrderUseCase orderUseCase;
    private final DtoMapper dtoMapper;

    public OrderController(OrderUseCase orderUseCase, DtoMapper dtoMapper) {
        this.orderUseCase = orderUseCase;
        this.dtoMapper = dtoMapper;
    }

    @PostMapping
    @Operation(summary = "Cree une nouvelle commande", description = "Cree une commande pour un client avec les produits specifies")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Commande creee avec succes"),
            @ApiResponse(responseCode = "400", description = "Donnees invalides")
    })
    public ResponseEntity<OrderDto> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        List<OrderLineRequest> lines = request.lines().stream()
                .map(line -> new OrderLineRequest(line.productId(), line.quantity()))
                .toList();

        var order = orderUseCase.createOrder(request.customerId(), lines);
        return ResponseEntity.status(HttpStatus.CREATED).body(dtoMapper.toOrderDto(order));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Met a jour le statut d'une commande", description = "Modifie le statut d'une commande existante")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Statut mis a jour avec succes"),
            @ApiResponse(responseCode = "400", description = "Statut invalide"),
            @ApiResponse(responseCode = "404", description = "Commande non trouvee")
    })
    public ResponseEntity<OrderDto> updateOrderStatus(
            @Parameter(description = "ID de la commande") @PathVariable Long id,
            @Valid @RequestBody UpdateStatusRequest request) {
        OrderStatus status = OrderStatus.valueOf(request.status());
        var order = orderUseCase.updateStatus(id, status);
        return ResponseEntity.ok(dtoMapper.toOrderDto(order));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Recupere une commande par ID", description = "Retourne les details d'une commande specifique")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Commande trouvee"),
            @ApiResponse(responseCode = "404", description = "Commande non trouvee")
    })
    public ResponseEntity<OrderDto> getOrderById(
            @Parameter(description = "ID de la commande") @PathVariable Long id) {
        return orderUseCase.getOrderById(id)
                .map(dtoMapper::toOrderDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
