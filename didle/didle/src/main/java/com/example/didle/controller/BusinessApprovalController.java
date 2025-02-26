package com.example.didle.controller;

import com.example.didle.model.BusinessApproval;
import com.example.didle.service.BusinessApprovalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/business-approvals")
public class BusinessApprovalController {
    private final BusinessApprovalService businessApprovalService;

    public BusinessApprovalController(BusinessApprovalService businessApprovalService) {
        this.businessApprovalService = businessApprovalService;
    }

    @PostMapping
    public ResponseEntity<BusinessApproval> createApproval(@RequestBody BusinessApproval approval) {
        BusinessApproval createdApproval = businessApprovalService.createApproval(approval);
        return new ResponseEntity<>(createdApproval, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BusinessApproval> getApprovalById(@PathVariable Long id) {
        BusinessApproval approval = businessApprovalService.getApprovalById(id);
        return ResponseEntity.ok(approval);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<BusinessApproval>> getPendingApprovals() {
        List<BusinessApproval> pendingApprovals = businessApprovalService.getPendingApprovals();
        return ResponseEntity.ok(pendingApprovals);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<BusinessApproval> updateApprovalStatus(
            @PathVariable Long id,
            @RequestParam BusinessApproval.ApprovalStatus status) {
        BusinessApproval updatedApproval = businessApprovalService.updateApprovalStatus(id, status);
        return ResponseEntity.ok(updatedApproval);
    }
}

