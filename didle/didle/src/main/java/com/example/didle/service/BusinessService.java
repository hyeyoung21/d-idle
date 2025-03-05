package com.example.didle.service;

import com.example.didle.model.Business;
import com.example.didle.model.BusinessApproval;
import com.example.didle.model.BusinessDTO;
import com.example.didle.model.User;
import com.example.didle.repository.BusinessApprovalRepository;
import com.example.didle.repository.BusinessRepository;
import com.example.didle.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class BusinessService {

    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;
    private final BusinessApprovalRepository approvalRepository;

    public BusinessService(BusinessRepository businessRepository, UserRepository userRepository, BusinessApprovalRepository approvalRepository) {
        this.businessRepository = businessRepository;
        this.userRepository = userRepository;
        this.approvalRepository = approvalRepository;
    }

    public Business createBusiness(BusinessDTO businessDTO) {

        // 사용자 확인
        User user = userRepository.findById(businessDTO.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // 비즈니스 엔티티 생성 및 저장
        Business business = new Business();

        business.setUser(user);
        business.setBusinessName(businessDTO.getBusinessName());
        business.setBusinessNumber(businessDTO.getBusinessNumber());
        business.setBusinessAddress(businessDTO.getBusinessAddress());
        business.setBusinessPhone(businessDTO.getBusinessPhone());

        Business savedBusiness = businessRepository.save(business);

        // 승인 상태 생성 및 저장
        BusinessApproval approval = new BusinessApproval();

        approval.setBusiness(savedBusiness);

        approval.setStatus(BusinessApproval.ApprovalStatus.PENDING);

        approvalRepository.save(approval);

        return savedBusiness; // 저장된 비즈니스 반환
    }


    public Business getBusinessByUserId(Long userId) {
        return businessRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Business not found for user"));
    }

    public BusinessDTO updateBusiness(Long userId, BusinessDTO businessDTO) {
        Business business = getBusinessByUserId(userId);

        // Update business fields
        business.setBusinessName(businessDTO.getBusinessName());
        business.setBusinessNumber(businessDTO.getBusinessNumber());
        business.setBusinessAddress(businessDTO.getBusinessAddress());
        business.setBusinessPhone(businessDTO.getBusinessPhone());

        // Save updated business
        business = businessRepository.save(business);

        // Create and populate DTO
        BusinessDTO updatedDTO = new BusinessDTO();
        updatedDTO.setId(business.getId());
        updatedDTO.setBusinessName(business.getBusinessName());
        updatedDTO.setBusinessNumber(business.getBusinessNumber());
        updatedDTO.setBusinessAddress(business.getBusinessAddress());
        updatedDTO.setBusinessPhone(business.getBusinessPhone());
        updatedDTO.setApprovalStatus(business.getApproval().getStatus().toString());

        return updatedDTO;
    }

}
