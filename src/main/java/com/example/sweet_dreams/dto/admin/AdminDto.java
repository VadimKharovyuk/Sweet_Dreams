package com.example.sweet_dreams.dto.admin;

import com.example.sweet_dreams.model.Admin;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AdminDto {
    private Long id;
    private String username;
    private String email;
    private Admin.AdminRole role;
    private boolean active;
    private LocalDateTime createdAt;
}
