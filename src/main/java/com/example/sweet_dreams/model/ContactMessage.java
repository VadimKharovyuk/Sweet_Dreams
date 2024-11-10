package com.example.sweet_dreams.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "contact_messages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "email", nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "subject", nullable = false)
    private ContactSubject subject;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "created_at")
    private LocalDateTime createdAt;



    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    // Enum для темы обращения
    public enum ContactSubject {
        GENERAL_INQUIRY("Общий вопрос"),
        ORDER_ISSUE("Проблема с заказом"),
        PRODUCT_QUESTION("Вопрос о продукте"),
        COOPERATION("Сотрудничество"),
        COMPLAINT("Жалоба"),
        SUGGESTION("Предложение"),
        OTHER("Другое");

        private final String displayName;

        ContactSubject(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}


