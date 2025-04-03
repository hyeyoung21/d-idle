package com.example.didle.repository;

import com.example.didle.model.vo.Business;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BusinessRepository extends JpaRepository<Business, Long> {

    // 사업자 등록 번호로 Business 찾기
    Optional<Business> findByBusinessNumber(String businessNumber);

    // 사업체 이름으로 Business 찾기
    Optional<Business> findByBusinessName(String businessName);

    Optional<Business> findByUsername(String username);
}

