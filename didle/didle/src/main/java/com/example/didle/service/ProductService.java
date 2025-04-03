package com.example.didle.service;

import com.example.didle.model.dto.ProductDTO;
import com.example.didle.model.vo.Category;
import com.example.didle.model.vo.Product;
import com.example.didle.repository.CartItemRepository;
import com.example.didle.repository.CategoryRepository;
import com.example.didle.repository.OrderItemRepository;
import com.example.didle.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
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
                          CategoryRepository categoryRepository
            ,
                          S3Client s3Client,
                          @Value("${spring.cloud.aws.s3.bucket}") String bucketName,
                          @Value("${spring.cloud.aws.region.static}") String region
    ) {
        this.productRepository = productRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartItemRepository = cartItemRepository;
        this.categoryRepository = categoryRepository;
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.region = region;
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

    public ProductDTO updateProduct(Long productId, ProductDTO productDTO, MultipartFile image, Long businessId) throws IOException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        if (!product.getBusinessId().equals(businessId)) {
            throw new EntityNotFoundException("Product does not belong to this business");
        }

        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setStockQuantity(productDTO.getStockQuantity());

        // 카테고리 설정
        if (productDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found"));
            product.setCategory(category);
        }

        // 이미지 처리
        if (image != null && !image.isEmpty()) {
            // 기존 이미지 삭제 (필요시)
            if (product.getImageUrl() != null) {
                String[] parts = product.getImageUrl().split("/");
                String fileName = parts[parts.length - 1];
                s3Client.deleteObject(DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(fileName)
                        .build());
            }

            // 1. 고유한 파일 이름 생성
            String newFileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();

            // 2. S3에 파일 업로드
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(newFileName)
                            .build(),
                    RequestBody.fromInputStream(image.getInputStream(), image.getSize())
            );

            // 3. 업로드된 파일의 URL 생성
            String newImageUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, newFileName);

            // 4. Product 엔티티에 이미지 URL 설정
            product.setImageUrl(newImageUrl);
        }

        productRepository.save(product);

        return ProductDTO.fromEntity(product);
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
//        if (image != null && !image.isEmpty()) {
//            // 1. 고유한 파일 이름 생성
//            String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
//
//            System.out.println("++++++++++++++++++++++++++++++++++++++++++" + s3Client.toString());
//            System.out.println("++++++++++++++++++++++++++++++++++++++++++" + bucketName);
//
//
//            // 2. S3에 파일 업로드
//            s3Client.putObject(
//                    PutObjectRequest.builder()
//                            .bucket(bucketName)
//                            .key(fileName)
//                            .build(),
//                    RequestBody.fromInputStream(image.getInputStream(), image.getSize())
//            );
//
//            System.out.println("++++++++++++++++++++++++++++++++++++++++++" + s3Client.toString());
//
//            // 3. 업로드된 파일의 URL 생성
//            String imageUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, fileName);
//
//            // 4. Product 엔티티에 이미지 URL 설정
//            product.setImageUrl(imageUrl);
//        }


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

    // 가장 많이 팔린 상품 조회
    public ProductDTO getMostSoldProduct() {
        Product mostSoldProduct = productRepository.findMostSoldProduct()
                .orElseThrow(() -> new RuntimeException("No products found"));
        return convertToDTO(mostSoldProduct);
    }

    // 특정 카테고리에서 상위 N개의 상품 조회
    public List<ProductDTO> getTopProductsByCategory(Long categoryId, int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by("createdAt").descending());
        return productRepository.findByCategoryId(categoryId, pageable)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


}

