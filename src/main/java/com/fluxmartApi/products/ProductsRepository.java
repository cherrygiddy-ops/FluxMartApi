package com.fluxmartApi.products;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductsRepository extends JpaRepository<ProductsEntity,Integer> {
    //Derived Queries
    List<ProductsEntity> findByName(String name);
    Optional<ProductsEntity> findById(int id);
    List<ProductsEntity> findTop5OrderByNameLike(String name);
    List<ProductsEntity> findByPriceBetween(BigDecimal min,BigDecimal max);

    @Query("SELECT  p FROM ProductsEntity p " +
            "JOIN FETCH p.category " +
            "LEFT JOIN FETCH p.images")
    List<ProductsEntity> findAllWithDetails();


}
