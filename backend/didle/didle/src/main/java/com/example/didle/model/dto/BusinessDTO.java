package com.example.didle.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessDTO {
    private Long id;

    // 로그인 정보 추가
    private String username;
    private String passwordHash;
    private String email;

    // 기존 필드 유지
    private String businessName;
    private String businessNumber;
    private String businessAddress;
    private String businessPhone;
    private String approvalStatus;
}
