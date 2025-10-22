package com.fluxmartApi.cart;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends JpaRepository<CartEntity, UUID> {

    @EntityGraph(attributePaths = "items.product")
    @Query("Select c from CartEntity c where c.id = :cartId")
    Optional<CartEntity>fetchCartWithItems(@Param("cartId") UUID cartId);
}
