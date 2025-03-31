package com.example.didle.service;

import com.example.didle.model.dto.OrderDTO;
import com.example.didle.model.dto.OrderItemDTO;
import com.example.didle.model.vo.Order;
import com.example.didle.model.vo.OrderItem;
import com.example.didle.model.vo.Product;
import com.example.didle.model.vo.User;
import com.example.didle.repository.OrderRepository;
import com.example.didle.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserService userService;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository, UserService userService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userService = userService;
    }

    public OrderDTO getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));
        return convertToDTO(order);
    }

    public List<OrderDTO> getOrdersByUserId(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setUserId(order.getUserId());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setStatus(order.getStatus());
        dto.setCreatedAt(order.getCreatedAt());

        // User 정보 설정
        User user = userService.getUserById(order.getUserId());
        dto.setCustomerName(user != null ? user.getUsername() : "Unknown User");

        dto.setOrderItems(order.getOrderItems().stream()
                .map(this::convertToOrderItemDTO)
                .collect(Collectors.toList()));
        return dto;
    }

    private OrderItemDTO convertToOrderItemDTO(OrderItem orderItem) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(orderItem.getId());

        Product product = orderItem.getProduct();
        if (product != null) {
            dto.setProductId(product.getId());
            dto.setProductName(product.getName());
        } else {
            dto.setProductName("판매 종료된 상품");
        }

        dto.setQuantity(orderItem.getQuantity());
        dto.setPrice(orderItem.getPrice());
        return dto;
    }

    @Transactional
    public Order createOrder(OrderDTO orderDTO) {
        Order order = new Order();
        order.setUserId(orderDTO.getUserId());
        order.setTotalPrice(orderDTO.getTotalPrice());
        order.setStatus(Order.OrderStatus.PENDING);

        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemDTO itemDTO : orderDTO.getOrderItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);

            try {
                Product product = productRepository.findById(itemDTO.getProductId())
                        .orElseThrow(() -> new EntityNotFoundException("Product not found"));
                orderItem.setProduct(product);
            } catch (EntityNotFoundException e) {
                // 상품이 없는 경우 null로 설정
                orderItem.setProduct(null);
            }

            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setPrice(itemDTO.getPrice());
            orderItems.add(orderItem);
        }

        order.setOrderItems(orderItems);
        return orderRepository.save(order);
    }

    public List<OrderDTO> getAllOrdersByBusinessId(Long businessId) {
        return orderRepository.findByBusinessId(businessId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public OrderDTO getOrderById(Long id, Long businessId) {
        Order order = orderRepository.findByIdAndBusinessId(id, businessId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        return convertToDTO(order);
    }

    public OrderDTO updateOrderStatus(Long id, String status, Long businessId) {
        Order order = orderRepository.findByIdAndBusinessId(id, businessId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        order.setStatus(Order.OrderStatus.valueOf(status));
        return convertToDTO(orderRepository.save(order));
    }
}
