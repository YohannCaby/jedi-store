package com.pokestore.db.repository;

import com.pokestore.db.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerJpaRepository extends JpaRepository<CustomerEntity, Long> {

    @Query("SELECT c FROM CustomerEntity c LEFT JOIN FETCH c.orders WHERE c.id = :id")
    Optional<CustomerEntity> findByIdWithOrders(@Param("id") Long id);
}
