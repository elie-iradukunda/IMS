package com.airtel.inventory.service.dto;

import java.time.LocalDate;

import com.airtel.inventory.domain.AssetCondition;

public record AssignmentRequest(
        Long assetId,
        Long departmentId,
        Long employeeId,
        String issuedBy,
        LocalDate issueDate,
        AssetCondition conditionAtIssue,
        String notes) {
}
