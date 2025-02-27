package com.example.didle.service;

import com.example.didle.model.*;
import com.example.didle.repository.OrderRepository;
import com.example.didle.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository, ProductService productService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.productService = productService;
    }

    public Order createOrder(OrderDTO orderDTO) {
        Order order = new Order();
        order.setUserId(orderDTO.getUserId());
        order.setStatus(Order.OrderStatus.PENDING);

        BigDecimal totalPrice = BigDecimal.ZERO;
        for (OrderItemDTO itemDTO : orderDTO.getOrderItems()) {
            Product product = productService.getProductById(itemDTO.getProductId());

            // Decrease product stock
            productService.decreaseStock(product.getId(), itemDTO.getQuantity());

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductId(product.getId());
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setPrice(product.getPrice());

            order.getOrderItems().add(orderItem);
            totalPrice = totalPrice.add(product.getPrice().multiply(new BigDecimal(itemDTO.getQuantity())));
        }

        order.setTotalPrice(totalPrice);
        return orderRepository.save(order);
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
    }

    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public Order updateOrderStatus(Long id, Order.OrderStatus status) {
        Order order = getOrderById(id);
        order.setStatus(status);
        return orderRepository.save(order);
    }

    public void cancelOrder(Long id) {
        Order order = getOrderById(id);
        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new IllegalStateException("Can only cancel pending orders");
        }
        order.setStatus(Order.OrderStatus.CANCELLED);

        // Restore product quantities
        for (OrderItem item : order.getOrderItems()) {
            productService.decreaseStock(item.getProductId(), -item.getQuantity());
        }

        orderRepository.save(order);
    }
}



