package com.pokestore.db.repository;

import com.pokestore.db.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderJpaRepository extends JpaRepository<OrderEntity, Long> {

    @Query("SELECT o FROM OrderEntity o LEFT JOIN FETCH o.lines l LEFT JOIN FETCH l.product WHERE o.id = :id")
    Optional<OrderEntity> findByIdWithLines(@Param("id") Long id);

    @Query("SELECT DISTINCT o FROM OrderEntity o LEFT JOIN FETCH o.lines WHERE o.customer.id = :customerId ORDER BY o.orderDate DESC")
    List<OrderEntity> findByCustomerId(@Param("customerId") Long customerId);
}
