package com.example.didle.model;

import lombok.Data;

@Data
public class OrderItemDTO {
    private Long productId;
    private Integer quantity;

    // getters and setters
}
