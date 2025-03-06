package com.example.didle.service;

import com.example.didle.model.*;
import com.example.didle.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BusinessService {

    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;
    private final BusinessApprovalRepository approvalRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public BusinessService(BusinessRepository businessRepository, UserRepository userRepository, BusinessApprovalRepository approvalRepository, OrderRepository orderRepository, ProductRepository productRepository) {
        this.businessRepository = businessRepository;
        this.userRepository = userRepository;
        this.approvalRepository = approvalRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
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
        System.out.println(userId);
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

    public DashboardDTO getDashboardData(Long userId) {
        Business business = businessRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Business not found for user ID: " + userId));

        Long businessId = business.getId();

        BigDecimal totalSales = orderRepository.sumTotalPriceByBusinessId(businessId);
        if (totalSales == null) totalSales = BigDecimal.ZERO;

        return DashboardDTO.builder()
                .totalSales(totalSales)
                .totalOrders(orderRepository.countByBusinessId(businessId))
                .productsInStock(productRepository.countByBusinessIdAndStockQuantityGreaterThan(businessId, 0))
                .recentOrders(orderRepository.findTop5ByBusinessIdOrderByCreatedAtDesc(businessId, PageRequest.of(0, 5))
                        .stream().map(this::convertToOrderDTO).collect(Collectors.toList()))
                .build();
    }




    private OrderDTO convertToOrderDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setUserId(order.getUserId());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setStatus(order.getStatus());
        dto.setCreatedAt(order.getCreatedAt());

        List<OrderItemDTO> orderItemDTOs = order.getOrderItems().stream()
                .map(item -> {
                    OrderItemDTO itemDTO = new OrderItemDTO();
                    itemDTO.setId(item.getId());
                    if (item.getProduct() != null) {
                        itemDTO.setProductId(item.getProduct().getId());
                        itemDTO.setProductName(item.getProduct().getName());
                    } else {
                        itemDTO.setProductName("판매 종료된 상품");
                    }
                    itemDTO.setQuantity(item.getQuantity());
                    itemDTO.setPrice(item.getPrice());
                    return itemDTO;
                })
                .collect(Collectors.toList());

        dto.setOrderItems(orderItemDTOs);
        return dto;
    }

    public BusinessDTO getBusinessDTOByUserId(Long userId) {
        Business business = getBusinessByUserId(userId);
        return convertToBusinessDTO(business);
    }

    private BusinessDTO convertToBusinessDTO(Business business) {
        BusinessDTO businessDTO = new BusinessDTO();
        businessDTO.setId(business.getId());
        businessDTO.setBusinessName(business.getBusinessName());
        businessDTO.setBusinessNumber(business.getBusinessNumber());
        businessDTO.setBusinessAddress(business.getBusinessAddress());
        businessDTO.setBusinessPhone(business.getBusinessPhone());
        if (business.getApproval() != null) {
            businessDTO.setApprovalStatus(business.getApproval().getStatus().toString());
        }
        return businessDTO;
    }

    public List<BusinessDTO> getAllBusinesses() {
        return businessRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public BusinessDTO updateBusinessApproval(Long id, String status) {
        Business business = businessRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Business not found"));
        business.getApproval().setStatus(BusinessApproval.ApprovalStatus.valueOf(status));
        Business updatedBusiness = businessRepository.save(business);
        return convertToDTO(updatedBusiness);
    }

    public void deleteBusiness(Long id) {
        if (!businessRepository.existsById(id)) {
            throw new EntityNotFoundException("Business not found");
        }
        businessRepository.deleteById(id);
    }

    private BusinessDTO convertToDTO(Business business) {
        BusinessDTO dto = new BusinessDTO();
        dto.setId(business.getId());
        dto.setBusinessName(business.getBusinessName());
        dto.setBusinessNumber(business.getBusinessNumber());
        dto.setBusinessAddress(business.getBusinessAddress());
        dto.setBusinessPhone(business.getBusinessPhone());
        dto.setApprovalStatus(business.getApproval().getStatus().toString());
        return dto;
    }
}
