package com.example.didle.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "full_name")
    private String fullName;

    private String phone;
    private String address;

    @Column(name = "user_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserType userType = UserType.CUSTOMER;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum UserType {
        CUSTOMER, BUSINESS, ADMIN
    }

    // 모든 필드를 포함한 생성자
    public User(String address, String email, String fullName, String passwordHash, String phone, UserType userType, String username, Long businessId) {
        this.address = address;
        this.email = email;
        this.fullName = fullName;
        this.passwordHash = passwordHash;
        this.phone = phone;
        this.userType = userType;
        this.username = username;
    }
}

