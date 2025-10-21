package com.fluxmartApi.categories;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/categories")
public class CategoriesController {

    private final  CategoryMapper mapper;
    private final CategoryRepository categoryRepository;

    @PostMapping
    public ResponseEntity<?> addCategory(@RequestBody CategoryRequestDto requestDto){
      var categoryEntity = mapper.toEntity(requestDto);
      categoryRepository.save(categoryEntity);
      return ResponseEntity.ok(HttpStatus.CREATED);
    }
}
