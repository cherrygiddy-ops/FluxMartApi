package com.fluxmartApi.cart;

import com.fluxmartApi.products.ProductNotFoundException;
import com.fluxmartApi.products.ProductsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final ProductsRepository productsRepository;

    public CartResponseDto createCart(){
       var enty= cartRepository.save(new CartEntity());
        return cartMapper.toDto(enty);
    }

    public CartResponseDto getCartDetails(UUID cartId){
       var cart= cartRepository.fetchCartWithItems(cartId).orElseThrow(CartNotFoundException::new);
       return cartMapper.toDto(cart);
    }

    public CartItemsDto addToCart(UUID cartId, Integer productId){
        var cart = cartRepository.fetchCartWithItems(cartId).orElseThrow(CartNotFoundException::new);
        var product =productsRepository.findById(productId).orElseThrow(ProductNotFoundException::new);
        var cartItem = cart.updateOrAddCartItem(product);
        cartRepository.save(cart);
        return cartMapper.toDto(cartItem);
    }

    public void deleteCartItem(UUID cartId, Integer productId) {
        var cart = cartRepository.fetchCartWithItems(cartId).orElseThrow(CartNotFoundException::new);
        cart.removeFromCart(productId);
        cartRepository.save(cart);
    }

    public CartItemsDto updateCartItem(UUID cartId, Integer productId,int quantity) {
        var cart = cartRepository.fetchCartWithItems(cartId).orElseThrow(CartNotFoundException::new);
        var cartItem = cart.getCartItems(productId);
        if (cartItem == null)throw new ProductNotFoundException();
        cartItem.setQuantity(quantity);
        cartRepository.save(cart);
        return cartMapper.toDto(cartItem);
    }

    public void clearCart(UUID cartId) {
        var cart = cartRepository.fetchCartWithItems(cartId).orElseThrow(CartNotFoundException::new);
        cart.clearCart();
        cartRepository.save(cart);
    }
}
