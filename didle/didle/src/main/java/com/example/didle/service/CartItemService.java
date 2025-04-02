package com.example.didle.service;

import com.example.didle.model.dto.CartItemDTO;
import com.example.didle.model.dto.OrderDTO;
import com.example.didle.model.dto.OrderItemDTO;
import com.example.didle.model.vo.CartItem;
import com.example.didle.model.vo.Product;
import com.example.didle.repository.CartItemRepository;
import com.example.didle.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class CartItemService {
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final OrderService orderService;

    public CartItemService(CartItemRepository cartItemRepository, ProductRepository productRepository, OrderService orderService) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.orderService = orderService;
    }

    public CartItem addToCart(Long userId, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        CartItem cartItem = new CartItem();
        cartItem.setUserId(userId);
        cartItem.setProduct(product);

        return cartItemRepository.save(cartItem);
    }

    public List<CartItemDTO> getCartItemsByUserId(Long userId) {
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        return cartItems.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private CartItemDTO convertToDTO(CartItem cartItem) {
        CartItemDTO dto = new CartItemDTO();
        dto.setId(cartItem.getId());
        dto.setProductId(cartItem.getProduct().getId());
        dto.setProductName(cartItem.getProduct().getName());
        dto.setPrice(cartItem.getProduct().getPrice());
        dto.setImageUrl(cartItem.getProduct().getImageUrl());
        return dto;
    }

    public void removeFromCart(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    public List<CartItem> addMultipleToCart(List<Long> productIds, Long userId) {
        List<CartItem> cartItems = productIds.stream()
                .map(productId -> {
                    Product product = productRepository.findById(productId)
                            .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));
                    CartItem cartItem = new CartItem();
                    cartItem.setProduct(product);
                    cartItem.setUserId(userId);
                    return cartItem;
                })
                .collect(Collectors.toList());

        return cartItemRepository.saveAll(cartItems);
    }

    @Transactional
    public void checkout(Long userId, Map<Long, Integer> productQuantities) {
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        BigDecimal totalPrice = BigDecimal.ZERO;
        List<OrderItemDTO> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            Integer quantity = productQuantities.get(product.getId());
            if (quantity != null && quantity > 0) {
                if (product.getStockQuantity() < quantity) {
                    throw new IllegalStateException("Not enough stock for product: " + product.getName());
                }
                product.setStockQuantity(product.getStockQuantity() - quantity);
                productRepository.save(product);

//                BigDecimal itemPrice = product.getPrice().multiply(BigDecimal.valueOf(quantity));
                BigDecimal itemPrice = product.getPrice();
                totalPrice = totalPrice.add(itemPrice).multiply(BigDecimal.valueOf(quantity));

                OrderItemDTO orderItemDTO = new OrderItemDTO();
                orderItemDTO.setProductId(product.getId());
                orderItemDTO.setQuantity(quantity);
                orderItemDTO.setPrice(itemPrice);
                orderItems.add(orderItemDTO);
            }
        }

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setUserId(userId);
        orderDTO.setTotalPrice(totalPrice);
        orderDTO.setOrderItems(orderItems);

        orderService.createOrder(orderDTO);
        cartItemRepository.deleteByUserId(userId);
    }
}
