package com.example.sweet_dreams.maper;

import com.example.sweet_dreams.dto.product.ProductCreateDto;
import com.example.sweet_dreams.dto.product.ProductDto;
import com.example.sweet_dreams.dto.product.ProductListDto;
import com.example.sweet_dreams.dto.product.ProductUpdateDto;
import com.example.sweet_dreams.model.Category;
import com.example.sweet_dreams.model.Product;
import com.example.sweet_dreams.model.Review;
import com.example.sweet_dreams.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductMapper {
    private final CategoryMapper categoryMapper;
    private final ImageService imageService;

    // Маппинг из ProductCreateDto в Product
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
            Category category = new Category();
            category.setId(dto.getCategoryId());
            product.setCategory(category);
        }

        // Установка ингредиентов
        if (dto.getIngredients() != null) {
            product.setIngredients(new HashSet<>(dto.getIngredients()));
        }

        // Обработка изображения
        if (dto.getMainImage() != null && !dto.getMainImage().isEmpty()) {
            product.setMainImage(imageService.convertToBytes(dto.getMainImage()));
        }

        // Установка цен для разных размеров
        if (dto.getSizePrices() != null) {
            product.setSizePrices(new HashMap<>(dto.getSizePrices()));
        }

        product.setAvailable(true);
        product.setReviewsCount(0);

        return product;
    }

    // Маппинг из Product в ProductDto
    public ProductDto toDto(Product product) {
        if (product == null) return null;

        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .category(product.getCategory() != null ?
                        categoryMapper.toDto(product.getCategory()) : null)
                .size(product.getSize())
                .weight(product.getWeight())
                .ingredients(product.getIngredients())
                .custom(product.isCustom())
                .mainImageBase64(product.getMainImage() != null ?
                        imageService.convertToBase64(product.getMainImage()) : null)
                .available(product.isAvailable())
                .preparationTimeHours(product.getPreparationTimeHours())
                .minimumOrderTimeHours(product.getMinimumOrderTimeHours())
//                .averageRating(calculateAverageRating(product.getAverageRating()))
                .reviewsCount(product.getReviewsCount())
                .sizePrices(product.getSizePrices())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    // Маппинг из Product в ProductListDto
    public ProductListDto toListDto(Product product) {
        if (product == null) return null;

        return ProductListDto.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .mainImage(product.getMainImage() != null ?
                        imageService.convertToBase64(product.getMainImage()) : null)
                .available(product.isAvailable())
//                .averageRating(calculateAverageRating(product.getAverageRating()))
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
        if (dto.getIngredients() != null) product.setIngredients(dto.getIngredients());

        product.setCustom(dto.isCustom());
        product.setAvailable(dto.isAvailable());

        if (dto.getPreparationTimeHours() != null)
            product.setPreparationTimeHours(dto.getPreparationTimeHours());
        if (dto.getMinimumOrderTimeHours() != null)
            product.setMinimumOrderTimeHours(dto.getMinimumOrderTimeHours());
        if (dto.getSizePrices() != null)
            product.setSizePrices(dto.getSizePrices());

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
