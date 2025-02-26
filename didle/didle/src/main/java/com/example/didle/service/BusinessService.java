package com.example.didle.service;

import com.example.didle.model.Business;
import com.example.didle.model.BusinessApproval;
import com.example.didle.model.BusinessDTO;
import com.example.didle.model.User;
import com.example.didle.repository.BusinessApprovalRepository;
import com.example.didle.repository.BusinessRepository;
import com.example.didle.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class BusinessService {
    private final BusinessRepository businessRepository;
    private final BusinessApprovalRepository businessApprovalRepository;
    private final UserRepository userRepository;

    public BusinessService(BusinessRepository businessRepository,
                           BusinessApprovalRepository businessApprovalRepository,
                           UserRepository userRepository) {
        this.businessRepository = businessRepository;
        this.businessApprovalRepository = businessApprovalRepository;
        this.userRepository = userRepository;
    }

    public Business createBusiness(BusinessDTO businessDTO) {
        User user = userRepository.findById(businessDTO.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Business business = new Business();
        business.setUser(user);
        business.setBusinessName(businessDTO.getBusinessName());
        business.setBusinessNumber(businessDTO.getBusinessNumber());
        business.setBusinessAddress(businessDTO.getBusinessAddress());
        business.setBusinessPhone(businessDTO.getBusinessPhone());

        Business savedBusiness = businessRepository.save(business);

        System.out.println(savedBusiness);

        BusinessApproval approval = new BusinessApproval();
        approval.setBusiness(savedBusiness);
        approval.setStatus(BusinessApproval.ApprovalStatus.PENDING);
        businessApprovalRepository.save(approval);

        return savedBusiness;
    }

    public Business getBusinessById(Long id) {
        return businessRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Business not found"));
    }

    public List<Business> getAllBusinesses() {
        return businessRepository.findAll();
    }

    public Business updateBusiness(Business business) {
        return businessRepository.save(business);
    }

    public void deleteBusiness(Long id) {
        businessRepository.deleteById(id);
    }
}

