package com.example.didle.repository;

import com.example.didle.model.vo.BusinessApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusinessApprovalRepository extends JpaRepository<BusinessApproval, Long> {
    List<BusinessApproval> findByStatus(BusinessApproval.ApprovalStatus status);
}

