package com.example.sweet_dreams.repository;

import com.example.sweet_dreams.model.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface DiscountRepository extends JpaRepository<Discount, Long> {

    @Query("SELECT d FROM Discount d WHERE d.active = true AND d.validUntil > :now")
    List<Discount> findActiveDiscounts(LocalDateTime now);

    @Query("SELECT d FROM Discount d " +
            "WHERE d.active = true " +
            "AND d.validFrom <= :now " +
            "AND d.validUntil > :now " +
            "AND (d.maxUsageCount IS NULL OR d.currentUsageCount < d.maxUsageCount)")
    List<Discount> findValidDiscounts(LocalDateTime now);

    @Query("SELECT d FROM Discount d WHERE d.code = UPPER(:code)")
    Optional<Discount> findByCodeIgnoreCase(String code);

    boolean existsByCode(String code);

}
