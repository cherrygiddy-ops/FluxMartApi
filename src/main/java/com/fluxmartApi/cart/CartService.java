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

    public CartItemsDto addToCart(UUID cartId, AddToCartRequest request){
        var cart = cartRepository.fetchCartWithItems(cartId).orElseThrow(CartNotFoundException::new);
        var product =productsRepository.findById(request.getProductId()).orElseThrow(ProductNotFoundException::new);
        var cartItem = cart.getCartItemsEntity(product);
        cartRepository.save(cart);
        return cartMapper.toDto(cartItem);
    }
}
