package com.example.didle.model.vo;

import lombok.Data;

@Data
public class UpdateOrderStatusRequest {
    private Long orderId;
    private String status;

}
