package com.example.didle.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter

public class OrderItemDTO {
    private Long id;
    private Long productId;
    private String productName; // 상품 이름 추가
    private Integer quantity;
    private BigDecimal price;
}
