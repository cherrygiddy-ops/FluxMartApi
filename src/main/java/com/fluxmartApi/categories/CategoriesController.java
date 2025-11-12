package com.fluxmartApi.categories;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/categories")
public class CategoriesController {
    private final CategoryService service;

    @PostMapping
    public ResponseEntity<?> addCategory(@RequestBody CategoryRequestDto requestDto){
        service.addCategory(requestDto);
      return ResponseEntity.ok(HttpStatus.CREATED);
    }

    @GetMapping
    public  ResponseEntity<?> getAllCategories(){
        return ResponseEntity.ok(service.getAllCategories());
    }
}
