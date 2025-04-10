package com.example.didle.service;

import com.example.didle.model.vo.*;
import com.example.didle.repository.BusinessRepository;
import com.example.didle.repository.CategoryRepository;
import com.example.didle.repository.ProductRepository;
import com.example.didle.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Service
public class DummyDataService implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final ProductRepository productRepository;

    public DummyDataService(CategoryRepository categoryRepository, UserRepository userRepository, BusinessRepository businessRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        insertCategories();
        insertAdminUser();
        insertBusinesses();
        insertUsers();
        insertProducts();
    }

    private void insertCategories() {
        if (categoryRepository.count() == 0) { // 테이블에 데이터가 없는 경우만 실행
            List<Category> categories = Arrays.asList(
                    new Category("전자제품", "컴퓨터, 스마트폰, TV 등의 전자기기"),
                    new Category("가구", "거실, 침실, 주방, 사무실용 가구"),
                    new Category("스포츠용품", "운동기구, 스포츠웨어, 야외활동 용품")
//                    ,
//                    new Category("뷰티/화장품", "스킨케어, 메이크업, 향수 등"),
//                    new Category("주방용품", "조리도구, 식기, 주방가전 등"),
//                    new Category("완구/취미", "장난감, 보드게임, 취미용품 등"),
//                    new Category("생활용품", "청소용품, 욕실용품, 수납용품 등"),
//                    new Category("도서", "소설, 자기계발서, 전문서적 등 모든 종류의 책"),
//                    new Category("의류", "남성, 여성, 아동을 위한 모든 종류의 의류"),
//                    new Category("식품", "신선식품, 가공식품, 음료 등")
            );

            categoryRepository.saveAll(categories);
            System.out.println("Categories inserted successfully!");
        } else {
            System.out.println("Categories already exist. Skipping insertion.");
        }
    }

    private void insertAdminUser() {
        if (!userRepository.existsByEmail("관리자@관리자")) { // 이메일 기준으로 중복 확인
            User adminUser = new User();
            adminUser.setAddress("관리자");
            adminUser.setEmail("관리자@관리자");
            adminUser.setFullName("관리자");
            adminUser.setPasswordHash("rhksflwk"); // 실제로는 암호화된 비밀번호를 사용해야 함
            adminUser.setPhone("000-0000-0000");
            adminUser.setUserType(User.UserType.ADMIN); // Enum 값으로 설정
            adminUser.setUsername("admin");

            userRepository.save(adminUser);
            System.out.println("Admin user inserted successfully!");
        } else {
            System.out.println("Admin user already exists. Skipping insertion.");
        }
    }

    private void insertUsers() {
        if (userRepository.count() == 1) { // 유저 테이블에 데이터가 없는 경우만 실행

            User user1 = new User();
            user1.setAddress("수원시");
            user1.setEmail("hogeun4656@gmail.com");
            user1.setFullName("류호근");
            user1.setPasswordHash("fbghrms");
            user1.setPhone("010-1111-1111");
            user1.setUserType(User.UserType.CUSTOMER); // Enum 값으로 설정
            user1.setUsername("hogeun");
            userRepository.save(user1);

            User user2 = new User();
            user2.setAddress("인천시");
            user2.setEmail("seomjin@gmail.com");
            user2.setFullName("김섬진");
            user2.setPasswordHash("rlatjawls");
            user2.setPhone("010-2222-2222");
            user2.setUserType(User.UserType.CUSTOMER); // Enum 값으로 설정
            user2.setUsername("seomjin");
            userRepository.save(user2);

            User user3 = new User();
            user3.setAddress("서울시");
            user3.setEmail("dongju@gmail.com");
            user3.setFullName("이동주");
            user3.setPasswordHash("dlehdwn");
            user3.setPhone("010-3333-3333");
            user3.setUserType(User.UserType.CUSTOMER); // Enum 값으로 설정
            user3.setUsername("dongju");
            userRepository.save(user3);

            User user4 = new User();
            user4.setAddress("서울시");
            user4.setEmail("sguki@gmail.com");
            user4.setFullName("김승욱");
            user4.setPasswordHash("rlatmddnr");
            user4.setPhone("010-4444-4444");
            user4.setUserType(User.UserType.CUSTOMER); // Enum 값으로 설정
            user4.setUsername("sguki");
            userRepository.save(user4);

            User user5 = new User();
            user5.setAddress("속초시");
            user5.setEmail("hyeyoung@gmail.com");
            user5.setFullName("권혜영");
            user5.setPasswordHash("rnjsgPdud");
            user5.setPhone("010-5555-5555");
            user5.setUserType(User.UserType.CUSTOMER); // Enum 값으로 설정
            user5.setUsername("hyeyoung");
            userRepository.save(user5);

            System.out.println("Users inserted successfully!");
        } else {
            System.out.println("Users already exist. Skipping insertion.");
        }
    }

    private void insertBusinesses() {
        if (businessRepository.count() == 0) { // 기업 테이블에 데이터가 없는 경우만 실행
            List<Business> businesses = Arrays.asList(
                    createBusiness("호근닷컴", "ghrmsektzja", "hogeun@gmail.com", "호근닷컴", "123-45-67890", "Seoul, Korea", "02-1234-5678"),
                    createBusiness("가구나라", "rkrnskfk", "gagunation@gmail.com", "가구나라", "234-56-78901", "Busan, Korea", "051-2345-6789"),
                    createBusiness("오늘은 스포츠", "dhsmfdmstmvhcm", "sports@gmail.com", "오늘은 스포츠", "345-67-89012", "Incheon, Korea", "032-3456-7890")
            );

            businessRepository.saveAll(businesses);
            System.out.println("Businesses inserted successfully!");
        } else {
            System.out.println("Businesses already exist. Skipping insertion.");
        }
    }

    private Business createBusiness(String username, String password, String email, String name, String number, String address, String phone) {
        Business business = new Business();
        business.setUsername(username);
        business.setPasswordHash(password); // 실제로는 암호화된 비밀번호를 사용해야 함
        business.setEmail(email);
        business.setBusinessName(name);
        business.setBusinessNumber(number);
        business.setBusinessAddress(address);
        business.setBusinessPhone(phone);

        BusinessApproval approval = new BusinessApproval();
        approval.setStatus(BusinessApproval.ApprovalStatus.APPROVED);
        approval.setBusiness(business);

        business.setApproval(approval);

        return business;
    }

    private void insertProducts() {
        if (productRepository.count() == 0) { // 물품 테이블에 데이터가 없는 경우만 실행
            List<Category> categories = categoryRepository.findAll(); // 카테고리 조회
            if (categories.isEmpty()) {
                System.out.println("No categories found. Please insert categories first.");
                return;
            }

            List<Product> products = Arrays.asList(
                    createProduct(1L, "Laptop", "High-performance laptop", new BigDecimal("1500.00"), 10, "/img/laptop.webp", categories.get(0)),
                    createProduct(1L, "Smartphone", "Latest model smartphone", new BigDecimal("800.00"), 20, "/img/smartphone.webp", categories.get(0)),
                    createProduct(1L, "Tablet", "Lightweight and powerful tablet", new BigDecimal("600.00"), 15, "/img/Tablet.webp", categories.get(0)),

                    createProduct(2L, "Dining Table", "Elegant wooden dining table", new BigDecimal("300.00"), 5, "/img/Dining.webp", categories.get(1)),
                    createProduct(2L, "Office Chair", "Ergonomic office chair", new BigDecimal("120.00"), 12, "/img/Chair.webp", categories.get(1)),
                    createProduct(2L, "Bookshelf", "Spacious wooden bookshelf", new BigDecimal("200.00"), 8, "/img/Bookshelf.png", categories.get(1)),

                    createProduct(3L, "Basketball", "Durable outdoor basketball", new BigDecimal("25.00"), 15, "/img/Basketball.jpg", categories.get(2)),
                    createProduct(3L, "Tennis Racket", "Lightweight tennis racket", new BigDecimal("75.00"), 20, "/img/Racket.webp", categories.get(2)),
                    createProduct(3L, "Yoga Mat", "Non-slip yoga mat for workouts", new BigDecimal("30.00"), 25, "/img/Yoga.png", categories.get(2))
            );


            productRepository.saveAll(products);
            System.out.println("Products inserted successfully!");
        } else {
            System.out.println("Products already exist. Skipping insertion.");
        }
    }

    private Product createProduct(Long businessId, String name, String description, BigDecimal price, Integer stockQuantity, String imageUrl, Category category) {
        Product product = new Product();
        product.setBusinessId(businessId); // 해당 물품을 소유하는 사업체 ID
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStockQuantity(stockQuantity);
        product.setImageUrl(imageUrl);
        product.setCategory(category); // 카테고리 설정
        return product;
    }
}
