package com.fluxmartApi.order;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(target = "orderItems",source = "orderItems")
  OrderResponseDto toDto (OrderEntity order);
}
