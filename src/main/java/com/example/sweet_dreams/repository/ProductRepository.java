package com.example.sweet_dreams.repository;

import com.example.sweet_dreams.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryId(Long categoryId);
    List<Product> findByNameContainingIgnoreCase(String keyword);

    List<Product> findByAvailableTrue();


    List<Product> findTop4ByCategoryIdAndAvailableTrueOrderByCreatedAtDesc(Long id);

}
