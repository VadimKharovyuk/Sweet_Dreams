package com.example.sweet_dreams.maper;

import com.example.sweet_dreams.dto.product.ProductCreateDto;
import com.example.sweet_dreams.dto.product.ProductDto;
import com.example.sweet_dreams.dto.product.ProductListDto;
import com.example.sweet_dreams.dto.product.ProductUpdateDto;
import com.example.sweet_dreams.exception.CategoryNotFoundException;
import com.example.sweet_dreams.model.Category;
import com.example.sweet_dreams.model.Product;
import com.example.sweet_dreams.model.Review;
import com.example.sweet_dreams.repository.CategoryRepository;
import com.example.sweet_dreams.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductMapper {
    private final CategoryMapper categoryMapper;
    private final ImageService imageService;
    private final CategoryRepository categoryRepository;

    public Product toEntity(ProductCreateDto dto) throws IOException {
        if (dto == null) return null;

        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setSize(dto.getSize());
        product.setWeight(dto.getWeight());
        product.setCustom(dto.isCustom());
        product.setPreparationTimeHours(dto.getPreparationTimeHours());
        product.setMinimumOrderTimeHours(dto.getMinimumOrderTimeHours());

        // Установка категории
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException("Category not found"));
            product.setCategory(category);
        }

        // Установка изображения
        if (dto.getMainImage() != null && !dto.getMainImage().isEmpty()) {
            product.setMainImage(imageService.convertToBytes(dto.getMainImage()));
        }

        product.setAvailable(true);
        product.setReviewsCount(0);
        product.setReviews(new ArrayList<>());

        return product;
    }

    public ProductDto toDto(Product product) {
        if (product == null) return null;

        ProductDto dto = ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .category(categoryMapper.toDto(product.getCategory()))
                .size(product.getSize())
                .weight(product.getWeight())
                .custom(product.isCustom())
                .available(product.isAvailable())
                .preparationTimeHours(product.getPreparationTimeHours())
                .minimumOrderTimeHours(product.getMinimumOrderTimeHours())
                .reviewsCount(product.getReviewsCount())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();

        if (product.getMainImage() != null) {
            dto.setMainImageBase64(imageService.convertToBase64(product.getMainImage()));
        }

        if (product.getReviews() != null && !product.getReviews().isEmpty()) {
            dto.setAverageRating(calculateAverageRating(product.getReviews()));
        }

        return dto;
    }

    public ProductListDto toListDto(Product product) {
        if (product == null) return null;

        return ProductListDto.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .mainImageBase64(product.getMainImage() != null ?
                        imageService.convertToBase64(product.getMainImage()) : null)
                .available(product.isAvailable())
                .averageRating(calculateAverageRating(product.getReviews()))
                .build();
    }

    // Обновление существующего Product из ProductUpdateDto
    public void updateEntityFromDto(ProductUpdateDto dto, Product product) {
        if (dto == null || product == null) return;

        if (dto.getName() != null) product.setName(dto.getName());
        if (dto.getDescription() != null) product.setDescription(dto.getDescription());
        if (dto.getPrice() != null) product.setPrice(dto.getPrice());
        if (dto.getSize() != null) product.setSize(dto.getSize());
        if (dto.getWeight() != null) product.setWeight(dto.getWeight());


        product.setCustom(dto.isCustom());
        product.setAvailable(dto.isAvailable());

        if (dto.getPreparationTimeHours() != null)
            product.setPreparationTimeHours(dto.getPreparationTimeHours());
        if (dto.getMinimumOrderTimeHours() != null)
            product.setMinimumOrderTimeHours(dto.getMinimumOrderTimeHours());


        // Обновление категории
        if (dto.getCategoryId() != null) {
            Category category = new Category();
            category.setId(dto.getCategoryId());
            product.setCategory(category);
        }
    }

    // Конвертация списка продуктов в список DTO
    public List<ProductDto> toDtoList(List<Product> products) {
        if (products == null) return Collections.emptyList();
        return products.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // Вспомогательный метод для расчета среднего рейтинга
    private Double calculateAverageRating(List<Review> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            return 0.0;
        }
        return reviews.stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0);
    }
}
