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
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
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
    private final S3Client s3Client;
    private final String bucketName;
    private final String region;

    public ProductService(ProductRepository productRepository,
                          OrderItemRepository orderItemRepository,
                          CartItemRepository cartItemRepository,
                          CategoryRepository categoryRepository,
                          S3Client s3Client,
                          @Value("${spring.cloud.aws.s3.bucket}") String bucketName,
                          @Value("${spring.cloud.aws.region.static}") String region) {
        this.productRepository = productRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartItemRepository = cartItemRepository;
        this.categoryRepository = categoryRepository;
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.region = region;
        System.out.println("++++++++++++++++++++++++++++++++++++bucketName: " + bucketName);
        System.out.println("++++++++++++++++++++++++++++++++++++region: " + region);
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
            // 1. 고유한 파일 이름 생성
            String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();

            System.out.println("++++++++++++++++++++++++++++++++++++++++++" + s3Client.toString());


            // 2. S3에 파일 업로드
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(fileName)
                            .build(),
                    RequestBody.fromInputStream(image.getInputStream(), image.getSize())
            );

            System.out.println("++++++++++++++++++++++++++++++++++++++++++" + s3Client.toString());

            // 3. 업로드된 파일의 URL 생성
            String imageUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, fileName);

            // 4. Product 엔티티에 이미지 URL 설정
            product.setImageUrl(imageUrl);
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

