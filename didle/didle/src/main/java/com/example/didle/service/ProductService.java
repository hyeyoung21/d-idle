package com.example.didle.service;

import com.example.didle.model.Product;
import com.example.didle.model.ProductDTO;
import com.example.didle.repository.CartItemRepository;
import com.example.didle.repository.OrderItemRepository;
import com.example.didle.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;

    public ProductService(ProductRepository productRepository, OrderItemRepository orderItemRepository, CartItemRepository cartItemRepository) {
        this.productRepository = productRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartItemRepository = cartItemRepository;
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product updateProduct(Product product) {
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        // CartItem의 product_id를 null로 설정
        cartItemRepository.nullifyProductId(id);

        // OrderItem의 product_id를 null로 설정
        orderItemRepository.nullifyProductId(id);

        // Product 삭제
        productRepository.deleteById(id);
    }

    public Product getMostSoldProduct() {
        List<Object[]> result = orderItemRepository.findMostSoldProductId();
        if (result.isEmpty()) {
            throw new RuntimeException("No products sold");
        }
        Long productId = (Long) result.get(0)[0];
        return productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public List<ProductDTO> getProductsByBusinessId(Long businessId) {
        List<Product> products = productRepository.findByBusinessId(businessId);
        return products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStockQuantity(product.getStockQuantity());
        // 필요한 다른 필드들도 설정
        return dto;
    }

    public ProductDTO addProduct(ProductDTO productDTO, Long businessId) {
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setStockQuantity(productDTO.getStockQuantity());
        product.setBusinessId(businessId);

        Product savedProduct = productRepository.save(product);

        return convertToDTO(savedProduct);
    }

}

