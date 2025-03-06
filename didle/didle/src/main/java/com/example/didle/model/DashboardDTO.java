package com.example.didle.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class DashboardDTO {
    private BigDecimal totalSales;
    private Long totalOrders;
    private Long productsInStock;
    private List<OrderDTO> recentOrders;
}
