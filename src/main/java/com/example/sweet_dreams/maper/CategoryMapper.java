package com.example.sweet_dreams.maper;

import com.example.sweet_dreams.dto.category.CategoryCreateDto;
import com.example.sweet_dreams.dto.category.CategoryDto;
import com.example.sweet_dreams.dto.category.CategoryUpdateDto;
import com.example.sweet_dreams.model.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CategoryMapper {
    public Category toEntity(CategoryCreateDto dto) {
        if (dto == null) return null;

        Category category = new Category();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setSlug(generateSlug(dto.getName()));
        return category;
    }

    public CategoryDto toDto(Category category) {
        if (category == null) return null;

        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .slug(category.getSlug())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }

    private String generateSlug(String input) {
        if (input == null) return "";
        return input.toLowerCase()
                .replaceAll("[^a-zа-я0-9\\s-]", "")
                .replaceAll("\\s+", "-");
    }

    public void updateEntityFromDto(CategoryUpdateDto dto, Category category) {
        if (dto == null || category == null) return;

        if (dto.getName() != null) category.setName(dto.getName());
        if (dto.getDescription() != null) category.setDescription(dto.getDescription());
    }

    public List<CategoryDto> toDtoList(List<Category> categories) {
        if (categories == null) return Collections.emptyList();
        return categories.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
