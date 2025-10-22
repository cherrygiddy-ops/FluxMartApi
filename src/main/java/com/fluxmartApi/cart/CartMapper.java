package com.fluxmartApi.cart;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {
   @Mapping(target = "totalPrice",expression = "java(entity.getTotalPrice())")
    CartResponseDto toDto(CartEntity entity);

    @Mapping(target = "totalPrice",expression = "java(cartItems.getTotalPrice())")
    CartItemsDto toDto (CartItemsEntity cartItems);
}
