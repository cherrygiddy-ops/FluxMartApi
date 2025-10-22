package com.fluxmartApi.cart;

import com.fluxmartApi.products.ProductsEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "cart_item")
@ToString
public class CartItemsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;


    @ManyToOne()
    @JoinColumn(name = "cart_id")
    private CartEntity cart;


    @ManyToOne()
    @JoinColumn(name = "product_id")
    private ProductsEntity product;

    @Column(name = "quantity")
    private Integer quantity;

    public  BigDecimal getTotalPrice(){
        return product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }
}
