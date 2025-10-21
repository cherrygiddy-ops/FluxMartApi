package com.fluxmartApi.categories;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private  final CategoryRepository categoryRepository;
    private final CategoryMapper mapper;

    public void addCategory(CategoryRequestDto requestDto) {
        var categoryEntity = mapper.toEntity(requestDto);
        categoryRepository.save(categoryEntity);
    }
}
