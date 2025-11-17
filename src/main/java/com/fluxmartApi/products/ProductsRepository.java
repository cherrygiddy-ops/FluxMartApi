package com.fluxmartApi.products;

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
    List<ProductsEntity> findByName(String name);
    Optional<ProductsEntity> findById(int id);
    List<ProductsEntity> findTop5OrderByNameLike(String name);
    List<ProductsEntity> findByPriceBetween(BigDecimal min,BigDecimal max);
    List<ProductsEntity> findByCategoryId(Byte categoryId);

    @Query("SELECT  p FROM ProductsEntity p " +
            "JOIN FETCH p.category " +
            "LEFT JOIN FETCH p.images where p.category.id=:categoryId " )
    List<ProductsEntity> findAllFilteredAndSortedProducts(@Param("categoryId")Byte categoryId,Pageable pageable,@Param("keyword")String keyword);

        @Query("SELECT p FROM ProductsEntity p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.descriptions) LIKE LOWER(CONCAT('%', :keyword, '%'))")
        List<ProductsEntity> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT  p FROM ProductsEntity p " +
            "JOIN FETCH p.category " +
            "LEFT JOIN FETCH p.images  ")
    List<ProductsEntity> findAllWithDetails(Pageable pageable);
    }
