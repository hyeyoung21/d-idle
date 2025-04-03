package com.example.didle.service;

import com.example.didle.model.dto.BusinessDTO;
import com.example.didle.model.dto.DashboardDTO;
import com.example.didle.model.dto.OrderDTO;
import com.example.didle.model.dto.OrderItemDTO;
import com.example.didle.model.vo.Business;
import com.example.didle.model.vo.BusinessApproval;
import com.example.didle.model.vo.Order;
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
    private final BusinessApprovalRepository approvalRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public BusinessService(BusinessRepository businessRepository, BusinessApprovalRepository approvalRepository, OrderRepository orderRepository, ProductRepository productRepository) {
        this.businessRepository = businessRepository;
        this.approvalRepository = approvalRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    // 비즈니스 생성 메서드 수정
    public Business createBusiness(BusinessDTO businessDTO) {
        // 비즈니스 엔티티 생성 및 저장
        Business business = new Business();
        business.setUsername(businessDTO.getUsername());
        business.setPasswordHash(businessDTO.getPasswordHash()); // 실제로는 비밀번호 해싱 적용 필요
        business.setEmail(businessDTO.getEmail());
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

        return savedBusiness;
    }

    // 비즈니스 인증 메서드 추가
    public Business authenticateBusiness(String username, String password) {
        Business business = businessRepository.findByUsername(username).orElse(null);
        if (business != null && business.getPasswordHash().equals(password)) { // 실제로는 비밀번호 해싱 검증 필요
            return business;
        }
        return null;
    }

    // getBusinessByUserId 메서드를 getBusinessById로 대체
    public Business getBusinessById(Long businessId) {
        return businessRepository.findById(businessId)
                .orElseThrow(() -> new EntityNotFoundException("Business not found"));
    }

    public BusinessDTO updateBusiness(Long businessId, BusinessDTO businessDTO) {
        Business business = getBusinessById(businessId);

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

    public DashboardDTO getDashboardData(Long businessId) {
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
