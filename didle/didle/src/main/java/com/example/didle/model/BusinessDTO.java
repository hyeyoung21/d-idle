package com.example.didle.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessDTO {
    private Long id;
    private Long userId; // 로그인한 사용자 ID
    private String businessName; // 사업체 이름
    private String businessNumber; // 사업자 등록 번호
    private String businessAddress; // 사업체 주소
    private String businessPhone; // 사업체 전화번호
    private String approvalStatus;
}
