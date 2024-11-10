package com.example.sweet_dreams.service.serviceImpl;

import com.example.sweet_dreams.dto.product.ProductCreateDto;
import com.example.sweet_dreams.dto.product.ProductDto;
import com.example.sweet_dreams.dto.product.ProductListDto;
import com.example.sweet_dreams.dto.product.ProductUpdateDto;
import com.example.sweet_dreams.exception.CategoryNotFoundException;
import com.example.sweet_dreams.exception.ProductCreationException;
import com.example.sweet_dreams.exception.ProductNotFoundException;
import com.example.sweet_dreams.maper.ProductMapper;
import com.example.sweet_dreams.model.Product;
import com.example.sweet_dreams.repository.CategoryRepository;
import com.example.sweet_dreams.repository.ProductRepository;
import com.example.sweet_dreams.service.ImageService;
import com.example.sweet_dreams.service.ProductService;
import lombok.RequiredArgsConstructor;
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
}
