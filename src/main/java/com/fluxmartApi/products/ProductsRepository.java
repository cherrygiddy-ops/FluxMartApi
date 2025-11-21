package com.fluxmartApi.products;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductsRepository extends JpaRepository<ProductsEntity,Integer> {
    //Derived Queries
    Optional<ProductsEntity> findByName(String name);

    Optional<ProductsEntity> findById(int id);

    List<ProductsEntity> findTop5OrderByNameLike(String name);

    List<ProductsEntity> findByPriceBetween(BigDecimal min, BigDecimal max);


    Page<ProductsEntity> findByCategoryId(Byte categoryId, Pageable pageable);

    Page<ProductsEntity> findByNameContainingIgnoreCase(String keyword, Pageable pageable);

    Page<ProductsEntity> findByCategoryIdAndNameContainingIgnoreCase(Byte categoryId, String keyword, Pageable pageable);

}