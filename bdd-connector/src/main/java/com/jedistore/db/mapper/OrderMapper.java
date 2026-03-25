package com.jedistore.db.mapper;

import com.jedistore.core.domain.entity.Order;
import com.jedistore.core.domain.entity.OrderLine;
import com.jedistore.core.domain.valueobject.OrderStatus;
import com.jedistore.db.entity.OrderEntity;
import com.jedistore.db.entity.OrderLineEntity;
import com.jedistore.db.entity.OrderStatusEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Mapper responsable de la conversion entre l'entité domaine {@link com.jedistore.core.domain.entity.Order}
 * et l'entité JPA {@link com.jedistore.db.entity.OrderEntity}.
 * <p>
 * Dépend de {@link CustomerMapper} et {@link ProductMapper} pour la conversion
 * des objets imbriqués. Gère également la conversion bidirectionnelle du statut
 * ({@code OrderStatus} ↔ {@code OrderStatusEntity}).
 * </p>
 */
@Component
public class OrderMapper {

    private final CustomerMapper customerMapper;
    private final ProductMapper productMapper;

    public OrderMapper(CustomerMapper customerMapper, ProductMapper productMapper) {
        this.customerMapper = customerMapper;
        this.productMapper = productMapper;
    }

    public Order toDomain(OrderEntity entity) {
        if (entity == null) {
            return null;
        }
        Order order = new Order();
        order.setId(entity.getId());
        order.setOrderDate(entity.getOrderDate());
        order.setCustomer(customerMapper.toDomain(entity.getCustomer()));
        order.setStatus(toOrderStatus(entity.getStatus()));
        order.setTotalAmount(entity.getTotalAmount());

        if (entity.getLines() != null) {
            order.setLines(entity.getLines().stream()
                    .map(this::toOrderLineDomain)
                    .collect(Collectors.toList()));
        }

        return order;
    }

    public OrderEntity toEntity(Order domain) {
        if (domain == null) {
            return null;
        }
        OrderEntity entity = new OrderEntity();
        entity.setId(domain.getId());
        entity.setOrderDate(domain.getOrderDate());
        entity.setCustomer(customerMapper.toEntity(domain.getCustomer()));
        entity.setStatus(toOrderStatusEntity(domain.getStatus()));
        entity.setTotalAmount(domain.getTotalAmount());

        if (domain.getLines() != null) {
            for (OrderLine line : domain.getLines()) {
                entity.addLine(toOrderLineEntity(line));
            }
        }

        return entity;
    }

    private OrderLine toOrderLineDomain(OrderLineEntity entity) {
        if (entity == null) {
            return null;
        }
        OrderLine line = new OrderLine();
        line.setId(entity.getId());
        line.setProduct(productMapper.toDomain(entity.getProduct()));
        line.setQuantity(entity.getQuantity());
        line.setUnitPrice(entity.getUnitPrice());
        return line;
    }

    private OrderLineEntity toOrderLineEntity(OrderLine domain) {
        if (domain == null) {
            return null;
        }
        OrderLineEntity entity = new OrderLineEntity();
        entity.setId(domain.getId());
        entity.setProduct(productMapper.toEntity(domain.getProduct()));
        entity.setQuantity(domain.getQuantity());
        entity.setUnitPrice(domain.getUnitPrice());
        return entity;
    }

    private OrderStatus toOrderStatus(OrderStatusEntity entity) {
        if (entity == null) {
            return null;
        }
        return switch (entity) {
            case IN_PROGRESS -> OrderStatus.IN_PROGRESS;
            case DELIVERED -> OrderStatus.DELIVERED;
            case CANCELLED -> OrderStatus.CANCELLED;
        };
    }

    private OrderStatusEntity toOrderStatusEntity(OrderStatus domain) {
        if (domain == null) {
            return null;
        }
        return switch (domain) {
            case IN_PROGRESS -> OrderStatusEntity.IN_PROGRESS;
            case DELIVERED -> OrderStatusEntity.DELIVERED;
            case CANCELLED -> OrderStatusEntity.CANCELLED;
        };
    }
}
