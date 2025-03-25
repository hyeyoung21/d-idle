package com.example.didle.service;

import com.example.didle.model.Category;
import com.example.didle.model.User;
import com.example.didle.repository.CategoryRepository;
import com.example.didle.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class DummyDataService implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public DummyDataService(CategoryRepository categoryRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        insertCategories();
        insertAdminUser();
    }

    private void insertCategories() {
        if (categoryRepository.count() == 0) { // 테이블에 데이터가 없는 경우만 실행
            List<Category> categories = Arrays.asList(
                    new Category("전자제품", "컴퓨터, 스마트폰, TV 등의 전자기기"),
                    new Category("의류", "남성, 여성, 아동을 위한 모든 종류의 의류"),
                    new Category("식품", "신선식품, 가공식품, 음료 등"),
                    new Category("가구", "거실, 침실, 주방, 사무실용 가구"),
                    new Category("도서", "소설, 자기계발서, 전문서적 등 모든 종류의 책"),
                    new Category("스포츠용품", "운동기구, 스포츠웨어, 야외활동 용품"),
                    new Category("뷰티/화장품", "스킨케어, 메이크업, 향수 등"),
                    new Category("주방용품", "조리도구, 식기, 주방가전 등"),
                    new Category("완구/취미", "장난감, 보드게임, 취미용품 등"),
                    new Category("생활용품", "청소용품, 욕실용품, 수납용품 등")
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
}
