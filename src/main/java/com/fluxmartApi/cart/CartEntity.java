package com.fluxmartApi.cart;

import com.fluxmartApi.products.ProductsEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity()
@Table(name = "cart")
@ToString
public class CartEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "created_at",insertable = false,updatable = false)
    private Date createdAt;

    @OneToMany(mappedBy = "cart",cascade = CascadeType.MERGE,orphanRemoval = true)
    @ToString.Exclude
    private Set<CartItemsEntity> items = new LinkedHashSet<>();

    public void  addToCart(CartItemsEntity cartItem){
        items.add(cartItem);
    }
    public void  removeFromCart(int productId){
       var cartItem = getCartItems(productId);
       if (cartItem != null){
           items.remove(cartItem);
           cartItem.setCart(null);
       }
    }
    public void clearCart(){
        items.clear();
    }
    public   CartItemsEntity getCartItems(int productId) {
       return items.stream().filter(carti->carti.getProduct().getId().equals(productId)).findFirst().orElse(null);
    }
    public   CartItemsEntity updateOrAddCartItem(ProductsEntity product) {
        var cartItem= getCartItems(product.getId());
        if (cartItem != null){
            cartItem.setQuantity(cartItem.getQuantity()+1);
        }else {
            cartItem = new CartItemsEntity();
            cartItem.setProduct(product);
            cartItem.setQuantity(1);
            cartItem.setCart(this);

            this.addToCart(cartItem);
        }
        return cartItem;
    }

    public BigDecimal getTotalPrice(){
        return items.stream().map(CartItemsEntity::getTotalPrice).reduce(BigDecimal.ZERO,BigDecimal::add);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}
