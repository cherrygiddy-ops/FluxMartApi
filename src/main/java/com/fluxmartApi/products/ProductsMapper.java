package com.fluxmartApi.products;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductsMapper {

    ProductsEntity toEntity (ProductsRequestDto productsRequestDto);

    @Mapping(target = "categoryId",source = "category.id")
    ProductsResponseDto toDto(ProductsEntity product);


    void  updateProduct(UpdateProductRequest request, @MappingTarget ProductsEntity productsEntity);
}
