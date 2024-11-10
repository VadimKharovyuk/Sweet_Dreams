package com.example.sweet_dreams.repository;

import com.example.sweet_dreams.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface AdminRepository extends JpaRepository<Admin,Long> {
    boolean existsByUsername(String username);

    Optional<Admin> findByUsername(String username);

}
