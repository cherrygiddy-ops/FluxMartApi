package com.fluxmartApi.products;

import com.fluxmartApi.cart.CartItemsEntity;
import com.fluxmartApi.categories.CategoryEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity()
@Table(name = "products")
@ToString
public class ProductsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "descriptions")
    private String descriptions;

    @Column(name = "quantity_in_stock")
    private Integer quantity;

    @Column(name = "unit_price")
    private BigDecimal price;

    @Column(name = "image_url")
    private  String imageUrl;

    @JoinColumn(name = "category_id")
    @ManyToOne(cascade = CascadeType.PERSIST)
    private CategoryEntity category;


}
