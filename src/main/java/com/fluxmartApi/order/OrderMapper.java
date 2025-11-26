package com.fluxmartApi.order;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(target = "orderItems",source = "orderItems")
    @Mapping(target = "paymentStatus",source = "paymentStatus")
    @Mapping(target = "deliveryStatus",source = "deliveryStatus")
    @Mapping(target = "cartId",source = "cart.id")
  OrderResponseDto toDto (OrderEntity order);
}
