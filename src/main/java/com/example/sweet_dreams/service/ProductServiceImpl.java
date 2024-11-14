package com.example.sweet_dreams.service;

import com.example.sweet_dreams.dto.product.*;
import com.example.sweet_dreams.exception.CategoryNotFoundException;
import com.example.sweet_dreams.exception.ProductCreationException;
import com.example.sweet_dreams.exception.ProductNotFoundException;
import com.example.sweet_dreams.maper.ProductMapper;
import com.example.sweet_dreams.model.Product;
import com.example.sweet_dreams.repository.CategoryRepository;
import com.example.sweet_dreams.repository.ProductRepository;
import com.example.sweet_dreams.service.serviceImpl.ProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

// ProductServiceImpl реализация
@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    private final ImageService imageService;

    @Override
    public ProductDto createProduct(ProductCreateDto productCreateDto) {
        validateCategory(productCreateDto.getCategoryId());
        try {
            Product product = productMapper.toEntity(productCreateDto);

            // Если есть изображение, преобразуем его в byte[]
            if (productCreateDto.getMainImage() != null && !productCreateDto.getMainImage().isEmpty()) {
                product.setMainImage(imageService.convertToBytes(productCreateDto.getMainImage()));
            }

            Product savedProduct = productRepository.save(product);
            return productMapper.toDto(savedProduct);
        } catch (IOException e) {
            throw new ProductCreationException("Ошибка при создании продукта: " + e.getMessage());
        }
    }


    @Override
    public ProductDto updateProduct(Long id, ProductUpdateDto productUpdateDto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Продукт не найден с id: " + id));

        if (productUpdateDto.getCategoryId() != null) {
            validateCategory(productUpdateDto.getCategoryId());
        }

        productMapper.updateEntityFromDto(productUpdateDto, product);
        Product updatedProduct = productRepository.save(product);
        return productMapper.toDto(updatedProduct);
    }

    @Override
    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Продукт не найден с id: " + id));
        return productMapper.toDto(product);
    }

    @Override
    public List<ProductListDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(productMapper::toListDto)
                .collect(Collectors.toList());
    }
    public List<ProductListDto> getAllAvailableProducts() {
        // Получаем все доступные продукты
        List<Product> availableProducts = productRepository.findByAvailableTrue();

        // Преобразуем в DTO и возвращаем
        return availableProducts.stream()
                .map(productMapper::toListDto)
                .collect(Collectors.toList());
    }
    @Override
    @Transactional(readOnly = true)
    public ProductDto findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
        return productMapper.toDto(product);
    }


    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException("Продукт не найден с id: " + id);
        }
        productRepository.deleteById(id);
    }
    // Метод для получения продуктов по категории
    @Override
    public List<ProductDto> getProductsByCategory(Long categoryId) {
        validateCategory(categoryId);
        List<Product> products = productRepository.findByCategoryId(categoryId);
        return products.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }



    @Override
    public List<ProductDto> searchProducts(String keyword) {
        List<Product> products = productRepository.findByNameContainingIgnoreCase(keyword);
        return products.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void toggleProductAvailability(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Продукт не найден с id: " + id));
        product.setAvailable(!product.isAvailable());
        productRepository.save(product);
    }

    private void validateCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new CategoryNotFoundException("Категория не найдена с id: " + categoryId);
        }
    }




    @Override
    public Page<ProductDto> findWithFilters(ProductFilterDto filterDto, int page, int size) {
        Sort sort = createSort(filterDto.getSortBy(), filterDto.getSortDirection());
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Product> products = productRepository.findWithFilters2(
                filterDto.getCategoryId(),
                filterDto.getMinPrice(),
                filterDto.getMaxPrice(),
                filterDto.getMinRating(),
                pageable
        );

        return products.map(productMapper::toDto);
    }


    @Override
    public List<ProductDto> getPopularProducts(ProductDto currentProduct, int limit) {
        Page<Product> popularProducts = productRepository.findTopByOrderByAverageRatingDesc(
                currentProduct.getId(),
                PageRequest.of(0, limit)
        );

        return popularProducts.getContent().stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }


    private Sort createSort(String sortBy, String sortDirection) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;

        String sortField = switch (sortBy) {
            case "price" -> "price";
            case "rating" -> "averageRating";
            default -> "id";
        };

        return Sort.by(direction, sortField);
    }




}
