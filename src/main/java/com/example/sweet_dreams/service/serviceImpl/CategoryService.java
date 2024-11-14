package com.example.sweet_dreams.service.serviceImpl;

import com.example.sweet_dreams.dto.category.CategoryCreateDto;
import com.example.sweet_dreams.dto.category.CategoryDto;
import com.example.sweet_dreams.dto.category.CategoryUpdateDto;
import com.example.sweet_dreams.dto.category.CategoryWithProductsDto;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(CategoryCreateDto categoryCreateDto);
    CategoryDto updateCategory(Long id, CategoryUpdateDto categoryUpdateDto);
    CategoryDto getCategoryById(Long id);
    List<CategoryDto> getAllCategories();
    void deleteCategory(Long id);

    List<CategoryWithProductsDto> getAllCategoriesWithProducts();

}
