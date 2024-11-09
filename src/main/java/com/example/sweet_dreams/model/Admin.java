package com.example.sweet_dreams.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "admins")
@Data
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    private AdminRole role;

    private boolean active = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public enum AdminRole {
        ADMIN,MANAGER
    }

}
