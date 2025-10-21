package com.fluxmartApi.categories;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryRequestDto {
   private Byte id;
   private String name;
}
