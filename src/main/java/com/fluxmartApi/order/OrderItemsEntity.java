package com.fluxmartApi.order;

import com.fluxmartApi.products.ProductsEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Table(name = "order_items")
@Entity
@Getter
@Setter
@ToString
public class OrderItemsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @JoinColumn(name = "order_id")
    @ManyToOne()
    private OrderEntity order;

    @JoinColumn(name = "product_id")
    @ManyToOne()
    private ProductsEntity product;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    @Column(name = "total_price")
    private BigDecimal totalPrice;


}
