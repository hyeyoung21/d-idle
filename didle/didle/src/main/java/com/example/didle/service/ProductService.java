package com.example.didle.service;

import com.example.didle.model.Category;
import com.example.didle.model.Product;
import com.example.didle.model.ProductDTO;
import com.example.didle.repository.CartItemRepository;
import com.example.didle.repository.CategoryRepository;
import com.example.didle.repository.OrderItemRepository;
import com.example.didle.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, OrderItemRepository orderItemRepository, CartItemRepository cartItemRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartItemRepository = cartItemRepository;
        this.categoryRepository = categoryRepository;
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

    public Product updateProduct(Product product, Long categoryId) {
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new EntityNotFoundException("Category not found"));
            product.setCategory(category);
        }
        return productRepository.save(product);
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


    public List<ProductDTO> getAllProducts(String searchKeyword, Long categoryId) {
        Specification<Product> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + searchKeyword.toLowerCase() + "%"));
            }

            if (categoryId != null) {
                predicates.add(criteriaBuilder.equal(root.get("category").get("id"), categoryId));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        List<Product> products = productRepository.findAll(spec);
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
        dto.setImageUrl(product.getImageUrl());  // 이미지 URL 설정
        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
            dto.setCategoryName(product.getCategory().getName());
        }
        dto.setBusinessId(product.getBusinessId());
        return dto;
    }


    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    public ProductDTO addProduct(ProductDTO productDTO, MultipartFile image, Long businessId) throws IOException {
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setStockQuantity(productDTO.getStockQuantity());
        product.setBusinessId(businessId);

        // 카테고리 설정
        if (productDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found"));
            product.setCategory(category);
        }

        // 이미지 처리
        if (image != null && !image.isEmpty()) {
            String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);
            Files.copy(image.getInputStream(), filePath);
            product.setImageUrl(contextPath + "/uploads/" + fileName);
        }

        Product savedProduct = productRepository.save(product);

        return convertToDTO(savedProduct);
    }


    public void deleteProduct(Long id) {
        // CartItem의 product_id를 null로 설정
        cartItemRepository.nullifyProductId(id);

        // OrderItem의 product_id를 null로 설정
        orderItemRepository.nullifyProductId(id);

        // Product 삭제
        productRepository.deleteById(id);
    }

}

