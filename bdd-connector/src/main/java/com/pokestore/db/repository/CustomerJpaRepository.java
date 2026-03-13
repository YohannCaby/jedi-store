package com.pokestore.db.repository;

import com.pokestore.db.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository JPA pour les clients.
 * <p>
 * Étend {@link JpaSpecificationExecutor} pour permettre les requêtes dynamiques
 * par critères (utilisé dans {@code CustomerRepositoryAdapter#search}).
 * </p>
 */
@Repository
public interface CustomerJpaRepository extends JpaRepository<CustomerEntity, Long>, JpaSpecificationExecutor<CustomerEntity> {

    /**
     * Charge un client avec toutes ses commandes en une seule requête (JOIN FETCH).
     * Évite le problème N+1 qui surviendrait avec un accès LAZY aux commandes.
     *
     * @param id identifiant du client
     * @return le client avec sa collection {@code orders} initialisée
     */
    @Query("SELECT c FROM CustomerEntity c LEFT JOIN FETCH c.orders WHERE c.id = :id")
    Optional<CustomerEntity> findByIdWithOrders(@Param("id") Long id);
}
