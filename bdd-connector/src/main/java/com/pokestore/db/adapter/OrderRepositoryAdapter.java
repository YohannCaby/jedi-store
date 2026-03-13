package com.pokestore.db.adapter;

import com.pokestore.core.domain.entity.Order;
import com.pokestore.core.port.out.OrderRepositoryPort;
import com.pokestore.db.entity.CustomerEntity;
import com.pokestore.db.entity.OrderEntity;
import com.pokestore.db.entity.OrderLineEntity;
import com.pokestore.db.entity.OrderStatusEntity;
import com.pokestore.db.mapper.OrderMapper;
import com.pokestore.db.repository.CustomerJpaRepository;
import com.pokestore.db.repository.OrderJpaRepository;
import com.pokestore.db.repository.ProductJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Adapter sortant implémentant le port {@link OrderRepositoryPort}.
 * <p>
 * La méthode {@code save} gère l'upsert manuellement : si la commande a déjà
 * un identifiant, l'entité JPA existante est rechargée pour bénéficier du contexte
 * de persistance Hibernate (éviter les doublons et les conflits d'état).
 * </p>
 * <p>
 * Les lignes de commande sont entièrement reconstruites à chaque sauvegarde
 * (clear + rebuild), car la collection est mappée avec {@code orphanRemoval = true}.
 * </p>
 */
@Component
public class OrderRepositoryAdapter implements OrderRepositoryPort {

    private final OrderJpaRepository orderJpaRepository;
    private final CustomerJpaRepository customerJpaRepository;
    private final ProductJpaRepository productJpaRepository;
    private final OrderMapper mapper;

    public OrderRepositoryAdapter(OrderJpaRepository orderJpaRepository,
                                  CustomerJpaRepository customerJpaRepository,
                                  ProductJpaRepository productJpaRepository,
                                  OrderMapper mapper) {
        this.orderJpaRepository = orderJpaRepository;
        this.customerJpaRepository = customerJpaRepository;
        this.productJpaRepository = productJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Order> findById(Long id) {
        return orderJpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Order> findByIdWithLines(Long id) {
        return orderJpaRepository.findByIdWithLines(id).map(mapper::toDomain);
    }

    @Override
    public List<Order> findByCustomerId(Long customerId) {
        return orderJpaRepository.findByCustomerId(customerId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public Order save(Order order) {
        OrderEntity entity;

        // Upsert : on recharge l'entité existante pour rester dans le même contexte JPA.
        // Cela permet à Hibernate de détecter les changements via dirty checking.
        if (order.getId() != null) {
            entity = orderJpaRepository.findById(order.getId())
                    .orElseGet(OrderEntity::new);
        } else {
            entity = new OrderEntity();
        }

        entity.setOrderDate(order.getOrderDate());
        entity.setTotalAmount(order.getTotalAmount());

        if (order.getStatus() != null) {
            // Conversion du statut domaine → statut JPA (même nom, enums distincts)
            entity.setStatus(OrderStatusEntity.valueOf(order.getStatus().name()));
        }

        if (order.getCustomer() != null && order.getCustomer().getId() != null) {
            // On charge l'entité customer managée pour que JPA puisse gérer la FK correctement
            CustomerEntity customerEntity = customerJpaRepository
                    .findById(order.getCustomer().getId())
                    .orElseThrow();
            entity.setCustomer(customerEntity);
        }

        // Reconstruction complète des lignes : orphanRemoval = true supprime automatiquement
        // les anciennes lignes non présentes dans la nouvelle collection
        entity.getLines().clear();
        if (order.getLines() != null) {
            for (var line : order.getLines()) {
                OrderLineEntity lineEntity = new OrderLineEntity();
                lineEntity.setQuantity(line.getQuantity());
                lineEntity.setUnitPrice(line.getUnitPrice());

                if (line.getProduct() != null && line.getProduct().getId() != null) {
                    var productEntity = productJpaRepository
                            .findById(line.getProduct().getId())
                            .orElseThrow();
                    lineEntity.setProduct(productEntity);
                }

                entity.addLine(lineEntity); // maintient la FK order_id sur la ligne
            }
        }

        var saved = orderJpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
}
