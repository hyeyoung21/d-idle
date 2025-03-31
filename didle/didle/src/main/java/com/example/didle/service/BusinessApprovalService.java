package com.example.didle.service;

import com.example.didle.model.vo.BusinessApproval;
import com.example.didle.repository.BusinessApprovalRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class BusinessApprovalService {
    private final BusinessApprovalRepository businessApprovalRepository;

    public BusinessApprovalService(BusinessApprovalRepository businessApprovalRepository) {
        this.businessApprovalRepository = businessApprovalRepository;
    }

    public BusinessApproval createApproval(BusinessApproval approval) {
        return businessApprovalRepository.save(approval);
    }

    public BusinessApproval getApprovalById(Long id) {
        return businessApprovalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Approval not found"));
    }

    public List<BusinessApproval> getPendingApprovals() {
        return businessApprovalRepository.findByStatus(BusinessApproval.ApprovalStatus.PENDING);
    }

    public BusinessApproval updateApprovalStatus(Long id, BusinessApproval.ApprovalStatus status) {
        BusinessApproval approval = getApprovalById(id);
        approval.setStatus(status);
        approval.setApprovedAt(LocalDateTime.now());
        return businessApprovalRepository.save(approval);
    }
}

