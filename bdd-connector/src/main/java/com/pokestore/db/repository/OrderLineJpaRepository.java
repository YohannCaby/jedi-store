package com.pokestore.db.repository;

import com.pokestore.db.entity.OrderLineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderLineJpaRepository extends JpaRepository<OrderLineEntity, Long> {
}
