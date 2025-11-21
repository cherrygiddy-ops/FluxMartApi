package com.fluxmartApi.cart;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {
   @Mapping(target = "totalPrice",expression = "java(entity.getTotalPrice())")

    CartResponseDto toDto(CartEntity entity);

    @Mapping(target = "totalPrice",expression = "java(cartItems.getTotalPrice())")
    @Mapping(target = "product.imageUrl",source = "product.imageUrl")
    CartItemsDto toDto (CartItemsEntity cartItems);
}
