package com.example.didle.model.vo;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "businesses")
@Data
public class Business {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "business_id")
    private Long id;

    // 로그인 정보 추가
    @Column(unique = true, nullable = false)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(unique = true, nullable = false)
    private String email;

    // 기존 필드 유지
    @Column(name = "business_name", nullable = false)
    private String businessName;

    @Column(name = "business_number", unique = true, nullable = false)
    private String businessNumber;

    @Column(name = "business_address", nullable = false)
    private String businessAddress;

    @Column(name = "business_phone", nullable = false)
    private String businessPhone;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "business", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private BusinessApproval approval;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
