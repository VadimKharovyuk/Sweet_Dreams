package com.example.sweet_dreams.service;

import com.example.sweet_dreams.dto.category.CategoryCreateDto;
import com.example.sweet_dreams.dto.category.CategoryDto;
import com.example.sweet_dreams.dto.category.CategoryUpdateDto;
import com.example.sweet_dreams.dto.category.CategoryWithProductsDto;
import com.example.sweet_dreams.exception.CategoryNotFoundException;
import com.example.sweet_dreams.maper.CategoryMapper;
import com.example.sweet_dreams.maper.ProductMapper;
import com.example.sweet_dreams.model.Category;
import com.example.sweet_dreams.model.Product;
import com.example.sweet_dreams.repository.CategoryRepository;
import com.example.sweet_dreams.repository.ProductRepository;
import com.example.sweet_dreams.service.serviceImpl.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final ProductMapper productMapper;
    private final ProductRepository productRepository;

    @Override
    public CategoryDto createCategory(CategoryCreateDto categoryCreateDto) {
        Category category = categoryMapper.toEntity(categoryCreateDto);
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toDto(savedCategory);
    }

    @Override
    public CategoryDto updateCategory(Long id, CategoryUpdateDto categoryUpdateDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));

        categoryMapper.updateEntityFromDto(categoryUpdateDto, category);
        Category updatedCategory = categoryRepository.save(category);
        return categoryMapper.toDto(updatedCategory);
    }

    @Override
    public CategoryDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
        return categoryMapper.toDto(category);
    }

    @Override
    public List<CategoryDto> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categoryMapper.toDtoList(categories);
    }

    @Override
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new CategoryNotFoundException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }
    public List<CategoryWithProductsDto> getAllCategoriesWithProducts() {
        return categoryRepository.findAll().stream()
                .map(category -> {
                    List<Product> products = productRepository.findTop4ByCategoryIdAndAvailableTrueOrderByCreatedAtDesc(category.getId());
                    BigDecimal minPrice = products.stream()
                            .map(Product::getPrice)
                            .min(BigDecimal::compareTo)
                            .orElse(BigDecimal.ZERO);

                    return CategoryWithProductsDto.builder()
                            .id(category.getId())
                            .name(category.getName())
                            .description(category.getDescription())
                            .products(products.stream()
                                    .map(productMapper::toListDto)
                                    .collect(Collectors.toList()))
                            .minPrice(minPrice)
                            .build();
                })
                .collect(Collectors.toList());
    }
}
