package com.example.sweet_dreams.service;

import com.example.sweet_dreams.dto.product.ProductCreateDto;
import com.example.sweet_dreams.dto.product.ProductDto;
import com.example.sweet_dreams.dto.product.ProductListDto;
import com.example.sweet_dreams.dto.product.ProductUpdateDto;

import java.util.List;

public interface ProductService {
    ProductDto createProduct(ProductCreateDto productCreateDto);
    ProductDto updateProduct(Long id, ProductUpdateDto productUpdateDto);
    ProductDto getProductById(Long id);
    List<ProductListDto> getAllProducts();
    void deleteProduct(Long id);
    List<ProductDto> getProductsByCategory(Long categoryId);
    List<ProductDto> searchProducts(String keyword);
    void toggleProductAvailability(Long id);

    List<ProductListDto> getAllAvailableProducts();

}
