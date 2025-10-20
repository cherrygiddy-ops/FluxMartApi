package com.fluxmartApi.products;

import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductsRepository extends JpaRepository<ProductsEntity,Integer> {
    //Derived Queries
    List<ProductsEntity> findByName(String name);
    Optional<ProductsEntity> findById(int id);
    List<ProductsEntity> findTop5OrderByNameLike(String name);
    List<ProductsEntity> findByPriceBetween(BigDecimal min,BigDecimal max);


}
