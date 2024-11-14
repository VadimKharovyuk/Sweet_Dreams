package com.example.sweet_dreams.service.serviceImpl;

import com.example.sweet_dreams.dto.product.*;
import com.example.sweet_dreams.model.Product;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {
    ProductDto createProduct(ProductCreateDto productCreateDto);

    ProductDto updateProduct( ProductUpdateDto productUpdateDto);

    ProductDto getProductById(Long id);

    List<ProductListDto> getAllProducts();

    void deleteProduct(Long id);

    List<ProductDto> getProductsByCategory(Long categoryId);

    List<ProductDto> searchProducts(String keyword);

    void toggleProductAvailability(Long id);

    List<ProductListDto> getAllAvailableProducts();

    ProductDto findById(Long productId);

    Page<ProductDto> findWithFilters(ProductFilterDto productFilterDto, int page, int size);


    List<ProductDto> getPopularProducts(ProductDto currentProduct, int limit);

}